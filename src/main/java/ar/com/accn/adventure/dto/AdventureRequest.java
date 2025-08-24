package ar.com.accn.adventure.dto;


import ar.com.accn.adventure.model.Complexity;
import ar.com.accn.adventure.model.Duration;
import ar.com.accn.adventure.model.Genre;
import ar.com.accn.adventure.model.Protagonist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;


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

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Complexity getComplexity() {
        return complexity;
    }

    public void setComplexity(Complexity complexity) {
        this.complexity = complexity;
    }

    public List<Protagonist> getProtagonists() {
        return protagonists;
    }

    public void setProtagonists(List<Protagonist> protagonists) {
        this.protagonists = protagonists;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}