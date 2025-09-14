package ar.com.accn.adventure.service;

import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class SummaryImageService {

    private final ImageModel imageModel;

    public SummaryImageService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public byte[] generatePng(String summary, String size, String quality) {
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("El campo 'summary' es obligatorio.");
        }

        // Configuración de opciones
        OpenAiImageOptions options = new OpenAiImageOptions();
        if (size != null && !size.isBlank()) {
            options.setSize(size); // ej: "1024x1024", "1792x1024", "1024x1792"
        }
        if (quality != null && !quality.isBlank()) {
            options.setQuality(quality); // ej: "standard" | "hd"
        }
        options.setResponseFormat("b64_json"); // aseguramos base64

        // Prompt de imagen
        ImagePrompt prompt = new ImagePrompt(summary, options);
        ImageResponse response = imageModel.call(prompt);

        // Convertimos base64 → bytes PNG
        String b64 = response.getResult().getOutput().getB64Json();
        return Base64.getDecoder().decode(b64);
    }
}



