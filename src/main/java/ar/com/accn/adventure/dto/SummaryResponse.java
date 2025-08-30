package ar.com.accn.adventure.dto;

public class SummaryResponse {

    private long sessionId;
    private String summary;

    public SummaryResponse() {
    }

    public SummaryResponse(long sessionId, String summary) {
        this.sessionId = sessionId;
        this.summary = summary;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}