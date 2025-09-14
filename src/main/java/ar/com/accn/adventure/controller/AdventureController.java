package ar.com.accn.adventure.controller;


import ar.com.accn.adventure.dto.*;

import ar.com.accn.adventure.service.AdventureService;
import ar.com.accn.adventure.service.StorySession;
import ar.com.accn.adventure.service.SummaryImageService;
import jakarta.validation.Valid;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/adventures")
public class AdventureController {

    private final AdventureService adventureService;
    private final OpenAiAudioSpeechModel speechModel;
    private final SummaryImageService summaryImageService;

    public AdventureController(AdventureService adventureService, OpenAiAudioSpeechModel speechModel, SummaryImageService summaryImageService) {
        this.adventureService = adventureService;
        this.speechModel = speechModel;
        this.summaryImageService = summaryImageService;
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

    @GetMapping("/summary/text/{sessionId}")
    public ResponseEntity<String> getSummary(@PathVariable long sessionId) {
        StorySession session = adventureService.generateSummary(sessionId);
        String summary = session.getSummaryText();
        if (summary == null || summary.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No summary available for session " + sessionId);
        }
        return ResponseEntity.ok(summary);
    }

    @GetMapping(value = "/summary/audio/{sessionId}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getSummaryAudio(@PathVariable long sessionId) {
        StorySession session = adventureService.generateSummary(sessionId);
        // Si no almacenaste el audio, genera aquí a partir de summaryText
        byte[] audio = session.getSummaryAudio();
        if (audio == null) {
            SpeechResponse resp = speechModel.call(new SpeechPrompt(session.getSummaryText()));
            audio = resp.getResult().getOutput();
            session.setSummaryAudio(audio);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"summary-" + sessionId + ".mp3\"")
                .body(audio);
    }

    @PostMapping(value = "/image-from-summary", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> imageFromSummary(@RequestBody ImageFromSummaryRequest req) {
        byte[] png = summaryImageService.generatePng(req.summary(), req.size(), req.quality());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"adventure.png\"")
                .body(png);
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}