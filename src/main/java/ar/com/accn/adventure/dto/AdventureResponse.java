package ar.com.accn.adventure.dto;

import java.util.List;


public class AdventureResponse {

    private final long sessionId;
    private final String narrative;
    private final List<String> choices;
    private final boolean finished;

    public AdventureResponse(long sessionId, String narrative, List<String> choices, boolean finished) {
        this.sessionId = sessionId;
        this.narrative = narrative;
        this.choices = choices;
        this.finished = finished;
    }

    public long getSessionId() {
        return sessionId;
    }

    public String getNarrative() {
        return narrative;
    }

    public List<String> getChoices() {
        return choices;
    }

    public boolean isFinished() {
        return finished;
    }
}