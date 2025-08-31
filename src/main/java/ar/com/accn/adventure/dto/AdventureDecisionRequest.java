package ar.com.accn.adventure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class AdventureDecisionRequest {

    @NotNull
    private Long sessionId;

    private Integer choiceIndex;

    private String choiceText;

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public void setChoiceIndex(Integer choiceIndex) {
        this.choiceIndex = choiceIndex;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }
}