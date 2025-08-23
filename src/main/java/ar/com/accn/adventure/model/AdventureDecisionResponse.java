package ar.com.accn.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdventureDecisionResponse {
    private String adventureId;
    private String updatedStory;
    private String imageUrl;
    private boolean isEnding; // true si ya terminó la historia
}
