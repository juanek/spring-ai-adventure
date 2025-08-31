package ar.com.accn.adventure.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class StorySession {

    @Getter
    private final long id;
    @Getter
    private final String conversationId;
    @Getter
    private int remainingDecisions;
    @Getter
    private final int optionsPerDecision;
    @Setter
    @Getter
    private boolean finished;
    @Setter
    @Getter
    private List<String> lastChoices;

    @Setter
    @Getter
    private String summaryText;
    @Setter
    @Getter
    private byte[] summaryAudio;


    private final StringBuilder narrative = new StringBuilder();

    public StorySession(long id, String conversationId, int remainingDecisions, int optionsPerDecision) {
        this.id = id;
        this.conversationId = conversationId;
        this.remainingDecisions = remainingDecisions;
        this.optionsPerDecision = optionsPerDecision;
        this.finished = false;
    }


    public void appendNarrative(String segment) {
        if (segment == null || segment.isBlank()) {
            return;
        }
        if (!narrative.isEmpty()) {
            narrative.append("\n\n");
        }
        narrative.append(segment.trim());
    }


    public String getNarrative() {
        return narrative.toString();
    }

    public void decrementRemainingDecisions() {
        if (remainingDecisions > 0) {
            remainingDecisions--;
        }
    }

}