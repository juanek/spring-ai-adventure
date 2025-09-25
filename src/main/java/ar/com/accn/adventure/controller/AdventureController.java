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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Tag(name = "Adventures", description = "API para crear y continuar historias interactivas, y generar resúmenes en texto, audio e imagen.")
@RestController
@RequestMapping("/adventures")
public class AdventureController {

    private final AdventureService adventureService;
    private final OpenAiAudioSpeechModel speechModel;
    private final SummaryImageService summaryImageService;

    public AdventureController(AdventureService adventureService,
                               OpenAiAudioSpeechModel speechModel,
                               SummaryImageService summaryImageService) {
        this.adventureService = adventureService;
        this.speechModel = speechModel;
        this.summaryImageService = summaryImageService;
    }

    @Operation(
            summary = "Crear una nueva aventura",
            description = "Inicializa una sesión de historia interactiva a partir de parámetros como género, protagonista y ubicación."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aventura creada",
                    content = @Content(schema = @Schema(implementation = AdventureResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request inválido")
    })
    @PostMapping
    public AdventureResponse createAdventure(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Parámetros iniciales de la aventura",
                    required = true
            )
            @Valid @RequestBody AdventureRequest request) {
        return adventureService.createAdventure(request);
    }

    @Operation(
            summary = "Tomar una decisión en la aventura",
            description = "Evalúa la decisión elegida por el usuario y devuelve el nuevo estado de la historia."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Decisión aplicada",
                    content = @Content(schema = @Schema(implementation = AdventureDecisionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Request inválido")
    })
    @PostMapping("/decision")
    public AdventureDecisionResponse makeDecision(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Decisión a aplicar sobre la sesión",
                    required = true
            )
            @Valid @RequestBody AdventureDecisionRequest request) {
        return adventureService.makeDecision(request);
    }

    @Operation(
            summary = "Obtener la historia completa",
            description = "Devuelve la historia acumulada de una sesión."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historia recuperada",
                    content = @Content(schema = @Schema(implementation = AdventureResponse.class))),
            @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    })
    @GetMapping("/{sessionId}")
    public AdventureResponse getFullStory(
            @Parameter(description = "Identificador de la sesión", example = "123")
            @PathVariable long sessionId) {
        return adventureService.getFullStory(sessionId);
    }

    @Operation(
            summary = "Obtener resumen en texto",
            description = "Genera (o recupera) el resumen de la sesión y lo devuelve en texto plano."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen generado",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "No hay resumen disponible para la sesión")
    })
    @GetMapping("/summary/text/{sessionId}")
    public ResponseEntity<String> getSummary(
            @Parameter(description = "Identificador de la sesión", example = "123")
            @PathVariable long sessionId) {
        StorySession session = adventureService.generateSummary(sessionId);
        String summary = session.getSummaryText();
        if (summary == null || summary.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No summary available for session " + sessionId);
        }
        return ResponseEntity.ok(summary);
    }

    @Operation(
            summary = "Obtener resumen en audio (MP3)",
            description = "Genera (o recupera) el audio del resumen de la sesión en formato MP3."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audio generado",
                    content = @Content(mediaType = "audio/mpeg",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    })
    @GetMapping(value = "/summary/audio/{sessionId}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getSummaryAudio(
            @Parameter(description = "Identificador de la sesión", example = "123")
            @PathVariable long sessionId) {
        StorySession session = adventureService.generateSummary(sessionId);
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

    @Operation(
            summary = "Generar imagen desde resumen",
            description = "Crea una imagen (PNG) a partir del texto resumen. Permite opcionalmente especificar tamaño y calidad."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen generada",
                    content = @Content(mediaType = "image/png",
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "400", description = "Request inválido")
    })
    @PostMapping(value = "/image-from-summary", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> imageFromSummary(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Resumen y parámetros de render (size, quality).",
                    required = true
            )
            @RequestBody ImageFromSummaryRequest req) {
        byte[] png = summaryImageService.generatePng(req.summary(), req.size(), req.quality());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"adventure.png\"")
                .body(png);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Argumento inválido",
                    content = @Content(mediaType = "text/plain"))
    })
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
