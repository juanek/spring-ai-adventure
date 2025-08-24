package ar.com.accn.adventure.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration that customises the ChatClient used by the
 * application.  A default system prompt is defined here so that
 * callers do not need to repeat it on every invocation.  The
 * configuration also wires a message window chat memory and an
 * advisor so that conversations maintain their history across
 * requests.
 */
@Configuration
public class ChatConfig {

    /**
     * Defines a ChatMemory bean that stores messages in memory using
     * the configured repository.  A window of recent messages is
     * retained (default 20); older messages are discarded except
     * system messages.  Spring Boot will auto-configure a
     * ChatMemoryRepository if one is on the classpath (for example
     * the in-memory repository).
     *
     * @param chatMemoryRepository the repository for persisting chat memory
     * @return a chat memory implementation
     */
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }

    /**
     * Creates a ChatClient bean with a default system prompt and
     * attaches the message chat memory advisor.  The system prompt
     * instructs the model to behave as an interactive adventure
     * narrator.  The default chat options set a moderate
     * temperature to encourage creativity.
     *
     * @param builder an autoconfigured ChatClient.Builder injected by Spring
     * @param chatMemory the ChatMemory bean used for maintaining conversation state
     * @return a configured ChatClient
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        // Compose the default system instructions.  This message guides
        // the model's behaviour across all conversations.  Since it
        // contains no variables it is set once at construction time.
        String systemPrompt = String.join("\n",
            "Eres un narrador de historias interactivas.",
            "El usuario te proporcionará el género, la duración (número de decisiones), la complejidad (número de opciones por decisión), los protagonistas y la ubicación.",
            "Crea aventuras en español que respeten estos parámetros.",
            "Para cada escena: describe la escena de forma detallada, luego presenta las opciones enumeradas para que el jugador elija sin revelar las consecuencias.",
            "Cuando no queden decisiones, concluye la historia con un final feliz para los protagonistas o un desenlace alternativo si corresponde.",
            "Mantén el tono adecuado al género y utiliza solo información proporcionada por el usuario.");
        // Attach the message chat memory advisor so that the memory is
        // retrieved and included automatically on each call.  Without
        // this advisor, the conversation history would not be used.
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return builder
                .defaultSystem(systemPrompt)
                .defaultOptions(OpenAiChatOptions.builder().temperature(0.8).build())
                .defaultAdvisors(memoryAdvisor)
                .build();
    }
}