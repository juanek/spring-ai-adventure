package ar.com.accn.adventure.dto;

import lombok.Getter;

import java.util.List;


@Getter
public class AdventureDecisionResponse {
    private final long sessionId;
    private final String narrative;
    private final List<String> choices;
    private final boolean finished;

    public AdventureDecisionResponse(long sessionId, String narrative, List<String> choices, boolean finished) {
        this.sessionId = sessionId;
        this.narrative = narrative;
        this.choices = choices;
        this.finished = finished;
    }

}