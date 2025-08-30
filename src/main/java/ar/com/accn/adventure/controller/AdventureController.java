package ar.com.accn.adventure.controller;


import ar.com.accn.adventure.dto.*;

import ar.com.accn.adventure.service.AdventureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/adventures")
public class AdventureController {

    private final AdventureService adventureService;

    public AdventureController(AdventureService adventureService) {
        this.adventureService = adventureService;
    }


    @PostMapping
    public AdventureResponse createAdventure(@Valid @RequestBody AdventureRequest request) {
        return adventureService.createAdventure(request);
    }


    @PostMapping("/decision")
    public AdventureDecisionResponse makeDecision(@Valid @RequestBody AdventureDecisionRequest request) {
        return adventureService.makeDecision(request);
    }


    @GetMapping("/{sessionId}")
    public AdventureResponse getFullStory(@PathVariable long sessionId) {
        return adventureService.getFullStory(sessionId);
    }

    @GetMapping("/summary/{sessionId}")
    public SummaryResponse getSummary(@PathVariable long sessionId) {
        return adventureService.generateSummary(sessionId);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}