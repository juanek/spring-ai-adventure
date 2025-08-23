package ar.com.accn.adventure.service;

import ar.com.accn.adventure.model.AdventureRequest;
import ar.com.accn.adventure.model.AdventureResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AdventureService {

    private final ChatClient chatClient;
    private final AdventurePrompt adventurePrompt;

    public AdventureService(ChatClient.Builder chatClientBuilder, AdventurePrompt adventurePrompt) {
        this.chatClient = chatClientBuilder.build();
        this.adventurePrompt = adventurePrompt;
    }

    public AdventureResponse generateAdventure(AdventureRequest request) {
        Map<String,Object> mapa = Map.of(
                "genre", request.getGenre(),
                "protagonistName", request.getProtagonistName(),
                "protagonistDescription", request.getProtagonistDescription(),
                "location", request.getLocation(),
                "duration", request.getDuration(),
                "complexity", request.getComplexity()
        );
        PromptTemplate prompt = adventurePrompt.buildPrompt();
        Prompt  prompt1 = prompt.create(mapa);

        String story = chatClient.prompt(prompt1).call().content();

        return new AdventureResponse(
                UUID.randomUUID().toString(),
                story,
                "https://placehold.co/600x400?text=Adventure+Image"
        );
    }
}

