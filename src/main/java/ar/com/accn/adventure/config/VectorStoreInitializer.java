package ar.com.accn.adventure.config;

import java.util.List;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Initializes the Milvus vector store with the features of the Megalodon car.
 * At application startup it reads the JSON file under {@code classpath:/data/}
 * and loads each entry as a document into the configured {@link VectorStore}.
 * This ensures that retrieval‑augmented generation can access information
 * about the car when generating stories.  If the documents already exist
 * the vector store will ignore duplicates based on ID.  See application
 * properties for the connection details and collection name.
 */
@Component
public class VectorStoreInitializer {

    private static final Logger logger = LoggerFactory.getLogger(VectorStoreInitializer.class);

    private final VectorStore vectorStore;

    @Autowired
    public VectorStoreInitializer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void loadMegalodonFeatures() {
        try {
            JsonReader reader = new JsonReader(new ClassPathResource("data/megalodon-features.json"), "content");
            List<Document> documents = reader.get();
            if (documents == null || documents.isEmpty()) {
                logger.warn("No se encontraron documentos de características del Megalodon para cargar");
                return;
            }
            vectorStore.add(documents);
            logger.info("Se cargaron {} características del Megalodon en la base de vectores", documents.size());
        } catch (Exception e) {
            logger.error("Error al cargar las características del Megalodon en la base de vectores", e);
        }
    }
}