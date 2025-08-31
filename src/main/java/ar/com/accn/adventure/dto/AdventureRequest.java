package ar.com.accn.adventure.dto;


import ar.com.accn.adventure.model.Complexity;
import ar.com.accn.adventure.model.Duration;
import ar.com.accn.adventure.model.Genre;
import ar.com.accn.adventure.model.Protagonist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class AdventureRequest {

    @NotNull
    private Genre genre;

    @NotNull
    private Duration duration;

    @NotNull
    private Complexity complexity;


    @Valid
    @Size(min = 1, max = 5)
    private List<Protagonist> protagonists;

    @NotNull
    @Size(min = 1)
    private String location;

}