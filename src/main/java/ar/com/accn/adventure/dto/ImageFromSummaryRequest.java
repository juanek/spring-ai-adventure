package ar.com.accn.adventure.dto;

public record ImageFromSummaryRequest(
        String summary,
        String size,
        String quality
) {}
