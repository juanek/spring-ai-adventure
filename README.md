# Spring AI Adventure

Aplicación **Spring Boot 3 + Spring AI** para generar aventuras interactivas con soporte de:
- Generación de **historias ramificadas**.
- Resúmenes en **texto**, **audio (MP3)** e **imagen (PNG)**.
- Persistencia de embeddings en **Milvus** (vector store).
- Exploración vía **Swagger UI**.

---

## ✨ Características

- **Historias interactivas** con decisiones en cada paso.
- **Resúmenes** automáticos de la aventura.
- **Audio** TTS (Text-To-Speech) del resumen (formato MP3).
- **Imagen** generada desde el resumen (PNG).
- **Integración con Milvus** para almacenamiento de embeddings.
- **Swagger UI** habilitado en `/swagger-ui.html`.

---

## 🧱 Tech Stack

- Java 17+ / 21
- Spring Boot 3.x
- Spring AI (Chat, Image, Audio, VectorStore)
- Milvus (Docker)
- OpenAI API
- Maven

---

## ⚙️ Configuración

El archivo [`application.properties`](./src/main/resources/application.properties) incluye la configuración base:

```properties
spring.application.name=spring-ai-adventure

# OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY:}
spring.ai.openai.model=gpt-4o

spring.ai.openai.image.options.size=1024x1024
spring.ai.openai.image.options.quality=standard
spring.ai.openai.image.options.response_format=b64_json

# Milvus
spring.ai.vectorstore.milvus.client.host=localhost
spring.ai.vectorstore.milvus.client.port=19530
spring.ai.vectorstore.milvus.client.username=root
spring.ai.vectorstore.milvus.client.password=Milvus
spring.ai.vectorstore.milvus.database-name=default
spring.ai.vectorstore.milvus.collection-name=megalodon_features
spring.ai.vectorstore.milvus.embedding-dimension=1536
spring.ai.vectorstore.milvus.index-type=IVF_FLAT
spring.ai.vectorstore.milvus.metric-type=COSINE
spring.ai.vectorstore.milvus.initialize-schema=true

# TTS
spring.ai.openai.audio.speech.options.model=tts-1
spring.ai.openai.audio.speech.options.voice=alloy
spring.ai.openai.audio.speech.options.response-format=mp3
spring.ai.openai.audio.speech.options.speed=1.0

# Swagger
springdoc.api-docs.enabled=true
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.enabled=true
```

---

## ▶️ Ejecución

### Opción A – Maven local
```bash
./mvnw clean package
./mvnw spring-boot:run
```

### Opción B – JAR
```bash
java -jar target/spring-ai-adventure-*.jar
```

### Opción C – Docker Compose

Incluye un `docker-compose.yml` con Milvus y dependencias:

```bash
docker compose up -d
```

---

## 📚 Endpoints principales

Basado en [`AdventureController.java`](./src/main/java/ar/com/accn/adventure/controller/AdventureController.java):

### 1) Crear aventura
**POST** `/adventures`
```json
{
  "genre": "FANTASY",
  "duration": "SHORT",
  "complexity": "LOW",
  "location": "bosque encantado",
  "protagonists": [
    { "name": "Aldo", "description": "Un valiente mago" }
  ]
}
```

### 2) Tomar decisión
**POST** `/adventures/decision`
```json
{
  "sessionId": 1,
  "choiceIndex": 0
}
```

### 3) Historia completa
**GET** `/adventures/{sessionId}`

### 4) Resumen en texto
**GET** `/adventures/summary/text/{sessionId}`

### 5) Resumen en audio (MP3)
**GET** `/adventures/summary/audio/{sessionId}`  
Devuelve un archivo `summary-{id}.mp3`.

### 6) Imagen desde resumen
**POST** `/adventures/image-from-summary`  
Body:
```json
{
  "summary": "Portada épica estilo ilustración: héroe con linterna frente a una cueva luminosa",
  "size": "1024x1024",
  "quality": "standard"
}
```
Response: `image/png`

---

## 🧪 Colección Postman

Se incluye [`SpringAI.postman_collection.json`](./SpringAI.postman_collection.json) con ejemplos para todos los endpoints:

- `generate adventures`
- `decision adventures`
- `adventures (GET)`
- `adventures summary (texto)`
- `adventures summary audio`
- `adventures summary image`

Importar en Postman → Collections → *Import*.

---

## 📖 Swagger

- OpenAPI JSON: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)  
- UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🛠 Troubleshooting

- **401/403** → revisa `OPENAI_API_KEY`.
- **Model not found** → cambia `spring.ai.openai.model` en `application.properties`.
- **Milvus no conecta** → revisa `docker compose logs` y credenciales (`root/Milvus`).
- **Imagen corrupta** → verifica que `response_format=b64_json` esté configurado.

---

## 📜 Licencia

MIT (o la que prefieras)
