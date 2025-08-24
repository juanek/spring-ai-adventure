package ar.com.accn.adventure.prompt;


import ar.com.accn.adventure.dto.AdventureRequest;
import ar.com.accn.adventure.model.Protagonist;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AdventurePrompt {


    public String render(AdventureRequest request) {
        String genre = request.getGenre().name().replace('_', ' ').toLowerCase();
        int decisions = request.getDuration().getNumberOfDecisions();
        int options = request.getComplexity().getNumberOfChoices();
        String location = request.getLocation();
        String protagonists = request.getProtagonists().stream()
            .map(this::formatProtagonist)
            .collect(Collectors.joining(", "));
        return String.format(
            "Eres un narrador de historias interactivas. " +
            "Crea una aventura de género %s situada en %s. " +
            "La aventura tendrá %d decisiones y cada decisión ofrecerá %d opciones. " +
            "Los protagonistas son: %s. " +
            "Describe la escena inicial de forma detallada y a continuación presenta %d opciones numeradas para que el jugador elija. " +
            "No reveles las consecuencias de las opciones, solo describe cada opción con claridad.",
            genre, location, decisions, options, protagonists, options);
    }

    private String formatProtagonist(Protagonist protagonist) {
        String desc = protagonist.getDescription();
        if (desc == null || desc.isBlank()) {
            return protagonist.getName();
        }
        return protagonist.getName() + " (" + desc + ")";
    }
}