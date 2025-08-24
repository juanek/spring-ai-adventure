package ar.com.accn.adventure.service;

import java.util.List;


public class StorySession {

    private final long id;
    private final String conversationId;
    private int remainingDecisions;
    private final int optionsPerDecision;
    private boolean finished;
    private List<String> lastChoices;


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
        if (narrative.length() > 0) {
            narrative.append("\n\n");
        }
        narrative.append(segment.trim());
    }


    public String getNarrative() {
        return narrative.toString();
    }

    public long getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public int getRemainingDecisions() {
        return remainingDecisions;
    }

    public void decrementRemainingDecisions() {
        if (remainingDecisions > 0) {
            remainingDecisions--;
        }
    }

    public int getOptionsPerDecision() {
        return optionsPerDecision;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public List<String> getLastChoices() {
        return lastChoices;
    }

    public void setLastChoices(List<String> lastChoices) {
        this.lastChoices = lastChoices;
    }
}