package ar.com.accn.adventure.model;

import lombok.Data;

@Data
public class AdventureRequest {
    private String genre;
    private String protagonistName;
    private String protagonistDescription;
    private String location;
    private String duration;   // short, medium, long
    private String complexity; // low, medium, high
}
