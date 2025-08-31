package ar.com.accn.adventure.dto;


public record SummaryResponse(long sessionId, String summary, String audioBase64) { }