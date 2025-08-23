package ar.com.accn.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdventureDecisionRequest {
    private String adventureId;
    private String currentStory;
    private String userChoice;
}
