package ar.com.accn.adventure.service;


import ar.com.accn.adventure.dto.AdventureDecisionRequest;
import ar.com.accn.adventure.dto.AdventureDecisionResponse;
import ar.com.accn.adventure.dto.AdventureRequest;
import ar.com.accn.adventure.dto.AdventureResponse;
import ar.com.accn.adventure.prompt.AdventurePrompt;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
// UserMessage and SystemMessage imports are intentionally omitted because
// we use the fluent API on ChatClient for constructing messages.
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsible for creating new adventures and continuing existing
 * sessions.  It orchestrates calls to the {@link ChatClient}, manages
 * conversation state via {@link ChatMemory} and parses the model
 * responses into structured data (narrative and options).  A map of
 * sessions keyed by their identifier holds the state for each active
 * story.
 */
@Service
@Validated
public class AdventureService {

    private static final Logger log = LoggerFactory.getLogger(AdventureService.class);

    private final ChatClient chatClient;
    private final AdventurePrompt adventurePrompt;

    /**
     * Pattern used to extract numbered options from the model output.  It
     * captures lines beginning with one or more digits followed by a
     * punctuation mark (period, parenthesis or dash) and returns the
     * remainder of the line as the choice text.
     */
    private static final Pattern OPTION_PATTERN = Pattern.compile("^(\\d+)[\\).\\-]\\s*(.+)$");

    /**
     * In‑memory store for active sessions.  The key is the session ID
     * returned to the client, and the value is the session state.  A
     * concurrent map is used because the service may be accessed by
     * multiple threads.
     */
    private final Map<Long, StorySession> sessions = new ConcurrentHashMap<>();

    /**
     * Atomic counter used to generate unique session identifiers.
     */
    private final AtomicLong idGenerator = new AtomicLong(1);

    public AdventureService(ChatClient chatClient, AdventurePrompt adventurePrompt) {
        this.chatClient = chatClient;
        this.adventurePrompt = adventurePrompt;
    }


    public AdventureResponse createAdventure(@Valid AdventureRequest request) {
        long sessionId = idGenerator.getAndIncrement();
        String conversationId = "session-" + sessionId;
        int remaining = request.getDuration().getNumberOfDecisions();
        int optionsPerDecision = request.getComplexity().getNumberOfChoices();
        StorySession session = new StorySession(sessionId, conversationId, remaining, optionsPerDecision);
        sessions.put(sessionId, session);

        // Render the prompt using the injected AdventurePrompt helper.
        String userPrompt = adventurePrompt.render(request);
        log.debug("Initial prompt for session {}: {}", sessionId, userPrompt);

        // Build chat options.  We set a temperature > 0 to encourage creative
        // storytelling but remain deterministic enough for reproducibility.
        ChatOptions chatOptions = OpenAiChatOptions.builder().temperature(0.8).build();

        // Invoke the model.  We pass the conversation ID via advisors so
        // that the ChatMemory stores and retrieves the conversation
        // history for this session.  The default system prompt is
        // configured on the ChatClient bean.
        String content = chatClient.prompt()
                .user(u -> u.text(userPrompt))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .options(chatOptions)
                .call()
                .content();

        // Parse the response into narrative and options.
        ParsedResponse parsed = parseContent(content);
        session.setLastChoices(parsed.choices);
        // The first decision has not been taken yet, so we do not
        // decrement remainingDecisions.  The story is considered
        // finished if there are no options returned.
        boolean finished = parsed.choices.isEmpty();
        session.setFinished(finished);

        // Append the initial narrative to the session so that the full
        // story can be retrieved later.
        session.appendNarrative(parsed.narrative);

        return new AdventureResponse(sessionId, parsed.narrative, parsed.choices, finished);
    }


    public AdventureDecisionResponse makeDecision(@Valid AdventureDecisionRequest request) {
        StorySession session = sessions.get(request.getSessionId());
        if (session == null) {
            throw new IllegalArgumentException("Sesión no encontrada: " + request.getSessionId());
        }
        if (session.isFinished()) {
            // If the story has already finished, return the last known
            // state without querying the model again.
            return new AdventureDecisionResponse(session.getId(), "La historia ya ha terminado.", Collections.emptyList(), true);
        }

        // Determine the selected choice.  If choiceIndex is provided we
        // validate it against the last returned options; otherwise we
        // accept the raw choice text.
        String selected;
        if (request.getChoiceIndex() != null) {
            int idx = request.getChoiceIndex();
            List<String> choices = session.getLastChoices();
            if (choices == null || idx < 0 || idx >= choices.size()) {
                throw new IllegalArgumentException("Índice de elección no válido: " + idx);
            }
            selected = choices.get(idx);
        } else if (request.getChoiceText() != null && !request.getChoiceText().isBlank()) {
            selected = request.getChoiceText();
        } else {
            throw new IllegalArgumentException("Debe proporcionar choiceIndex o choiceText");
        }

        // Decrease the remaining decision count since the user has now
        // consumed a decision.
        session.decrementRemainingDecisions();

        // Build a prompt to continue the story.  We instruct the model
        // to incorporate the chosen option and either present more
        // options or conclude the story based on remaining decisions.
        int remaining = session.getRemainingDecisions();
        int optionsPerDecision = session.getOptionsPerDecision();
        boolean shouldEnd = remaining <= 0;
        String userPrompt = buildContinuationPrompt(selected, remaining, optionsPerDecision, shouldEnd);
        log.debug("Continuation prompt for session {}: {}", session.getId(), userPrompt);

        ChatOptions chatOptions = OpenAiChatOptions.builder().temperature(0.8).build();
        String content = chatClient.prompt()
                .user(u -> u.text(userPrompt))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, session.getConversationId()))
                .options(chatOptions)
                .call()
                .content();

        ParsedResponse parsed = parseContent(content);
        session.setLastChoices(parsed.choices);
        // If no options returned or we should end anyway, mark finished
        boolean finished = shouldEnd || parsed.choices.isEmpty();
        session.setFinished(finished);

        // Append this narrative fragment to the complete story.
        session.appendNarrative(parsed.narrative);
        return new AdventureDecisionResponse(session.getId(), parsed.narrative, parsed.choices, finished);
    }

    public AdventureResponse getFullStory(long sessionId) {
        StorySession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Sesión no encontrada: " + sessionId);
        }
        return new AdventureResponse(session.getId(), session.getNarrative(), Collections.emptyList(), session.isFinished());
    }


    private String buildContinuationPrompt(String choice, int remainingDecisions, int optionsPerDecision, boolean shouldEnd) {
        if (shouldEnd) {
            return String.format(
                "El jugador eligió: %s. Continúa la historia en base a esta elección y concluye la aventura con un final feliz o un desenlace alternativo para los protagonistas. No presentes más opciones.",
                choice
            );
        }
        return String.format(
            "El jugador eligió: %s. Continúa la historia según esta elección. Describe la siguiente escena y luego presenta %d opciones numeradas para que el jugador elija. Quedan %d decisiones en total. No reveles las consecuencias de las opciones.",
            choice, optionsPerDecision, remainingDecisions
        );
    }


    private record ParsedResponse(String narrative, List<String> choices) {}


    private ParsedResponse parseContent(String content) {
        if (content == null) {
            return new ParsedResponse("", Collections.emptyList());
        }
        StringBuilder narrativeBuilder = new StringBuilder();
        List<String> choices = new ArrayList<>();
        String[] lines = content.split("\\r?\\n");
        boolean optionsStarted = false;
        for (String line : lines) {
            String trimmed = line.trim();
            Matcher matcher = OPTION_PATTERN.matcher(trimmed);
            if (matcher.matches()) {
                optionsStarted = true;
                String optionText = matcher.group(2).trim();
                choices.add(optionText);
            } else {
                if (!optionsStarted) {
                    narrativeBuilder.append(line).append("\n");
                }
            }
        }
        String narrative = narrativeBuilder.toString().trim();
        return new ParsedResponse(narrative, choices);
    }
}