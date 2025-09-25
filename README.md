# Spring AI Adventure

Pequeña app **Spring Boot + Spring AI** que genera aventuras interactivas y —en esta rama— **una imagen a partir del resumen** de la historia. Pensada como cápsula de entrenamiento JavaShark.

## ✨ Características

- **Generación de historias** con opciones (branching) usando `ChatClient`.
- **Imagen desde el resumen** (texto → imagen) usando `ImageClient` de Spring AI.
- Endpoints REST listos para probar con `curl` / Postman.
- Perfil de configuración simple por propiedades/variables de entorno.
- Docker Compose opcional para levantar dependencias comunes.

## 🧱 Tech Stack

- **Java 17+ / 21**
- **Spring Boot 3.x**
- **Spring Web**
- **Spring AI** (Chat + Image)
- **Maven**
- **Docker Compose** (opcional)

## 📂 Estructura (resumen)

```
spring-ai-adventure/
 ├─ src/main/java/.../adventure/   # Controllers, services, prompts (historia / imagen)
 ├─ src/main/resources/
 │   ├─ application.properties # Config por ambiente
 ├─ pom.xml
 └─ docker-compose.yml
```

## ⚙️ Configuración

### Variables de entorno sugeridas

```bash
# Clave del proveedor (OpenAI como ejemplo)
export SPRING_AI_OPENAI_API_KEY=sk-...

# Modelo de texto (opcional, depende de tu pom.yml)
export SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL=gpt-4o-mini

# Modelo de imagen (Spring AI soporta opciones para DALL·E/GPT)
export SPRING_AI_OPENAI_IMAGE_OPTIONS_MODEL=dall-e-3
# Tamaño (ver doc de imagenes: 1024x1024 | 1792x1024 | 1024x1792 para DALL·E 3)
export SPRING_AI_OPENAI_IMAGE_OPTIONS_SIZE=1024x1024
```

Si usas `application.yml`, puedes mapearlas como:

```yaml
spring:
  ai:
    openai:
      api-key: ${SPRING_AI_OPENAI_API_KEY}
      chat:
        options:
          model: ${SPRING_AI_OPENAI_CHAT_OPTIONS_MODEL:gpt-4o-mini}
      image:
        options:
          model: ${SPRING_AI_OPENAI_IMAGE_OPTIONS_MODEL:dall-e-3}
          size: ${SPRING_AI_OPENAI_IMAGE_OPTIONS_SIZE:1024x1024}
```

## ▶️ Ejecución

### Opción A – Maven (local)

```bash
./mvnw -q clean package
./mvnw spring-boot:run
```

### Opción B – Jar

```bash
java -jar target/spring-ai-adventure-*.jar
```

### Opción C – Docker (si aplica)

```bash
docker compose up -d
```

## 📚 Endpoints

### 1) Generar historia

**POST** `/adventures`

```json
{
  "genre": "fantasy",
  "protagonistName": "Luna",
  "protagonistDescription": "hacker curiosa",
  "location": "Buenos Aires subterráneo",
  "duration": "short",
  "complexity": "simple"
}
```

### 2) Continuar historia

**POST** `/adventures/{id}/choose`

```json
{ "choiceIndex": 1 }
```

### 3) Generar imagen desde resumen

**POST** `/adventures/image`

```json
{
  "summary": "Luna, una hacker curiosa, encuentra un portal de neón en el subte...",
  "style": "cinematic",
  "size": "1024x1024"
}
```

## 🧪 cURL rápido

```bash
curl -sS -X POST http://localhost:8080/adventures   -H "Content-Type: application/json"   -d '{"genre":"sci-fi","protagonistName":"Alex","protagonistDescription":"piloto","location":"Marte","duration":"short","complexity":"simple"}' | jq
```

## 🧩 Cómo está implementado

- **Prompts**: plantilla estructurada (system + user) para salida JSON.
- **Servicios**: `AdventureService` usa `ChatClient`.
- **Imagen**: `ImageService` usa `ImageClient`.
- **Config**: propiedades `spring.ai.openai.*` para texto e imagen.

