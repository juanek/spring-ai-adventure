package ar.com.accn.adventure.service;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdventurePrompt {

    private final String template = """
        You are a storyteller AI. 
        Create the beginning of an adventure story.
        
        Parameters:
        - Genre: {genre}
        - Protagonist: {protagonistName}, {protagonistDescription}
        - Location: {location}
        - Duration: {duration}
        - Complexity: {complexity}
        
        Begin the story and end with the first decision the protagonist must make. 
        Keep it concise and engaging.
        """;

    public PromptTemplate buildPrompt() {
        PromptTemplate promptTemplate = new PromptTemplate(template);
        return new PromptTemplate(template);
    }
}

