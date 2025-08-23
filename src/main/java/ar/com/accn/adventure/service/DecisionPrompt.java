package ar.com.accn.adventure.service;


import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DecisionPrompt {

    private final String template = """
        You are continuing an interactive adventure story.
        
        Previous story so far:
        {currentStory}
        
        The user chose: {userChoice}
        
        Continue the story based on this choice.
        If the story is reaching an ending (good or bad), clearly indicate it at the end.
        Always finish with the next decision options unless it is the ending.
        """;

    public PromptTemplate buildPrompt() {
        return new PromptTemplate(template);
    }
}

