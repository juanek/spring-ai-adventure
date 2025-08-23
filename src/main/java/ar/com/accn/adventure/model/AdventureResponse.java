package ar.com.accn.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdventureResponse {
    private String adventureId;
    private String story;
    private String imageUrl;
}
