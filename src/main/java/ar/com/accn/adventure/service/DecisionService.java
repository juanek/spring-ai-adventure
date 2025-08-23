package ar.com.accn.adventure.service;


import ar.com.accn.adventure.model.AdventureDecisionRequest;
import ar.com.accn.adventure.model.AdventureDecisionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DecisionService {

    private final ChatClient chatClient;
    private final DecisionPrompt decisionPrompt;

    public DecisionService(ChatClient.Builder chatClientBuilder, DecisionPrompt decisionPrompt) {
        this.chatClient = chatClientBuilder.build();
        this.decisionPrompt = decisionPrompt;
    }

    public AdventureDecisionResponse continueAdventure(AdventureDecisionRequest request) {
        Map<String,Object> mapa = Map.of(
                "currentStory", request.getCurrentStory(),
                "userChoice", request.getUserChoice()
        );
        PromptTemplate prompt = decisionPrompt.buildPrompt();
        Prompt prompt1 = prompt.create(mapa);

        String updatedStory = chatClient.prompt(prompt1).call().content();

        boolean isEnding = updatedStory.toLowerCase().contains("the end")
                || updatedStory.toLowerCase().contains("happy ending")
                || updatedStory.toLowerCase().contains("tragic ending");

        return new AdventureDecisionResponse(
                request.getAdventureId(),
                updatedStory,
                "https://placehold.co/600x400?text=Next+Scene",
                isEnding
        );
    }
}

