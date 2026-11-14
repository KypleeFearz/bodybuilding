# syntax=docker/dockerfile:1

# ---- 1. Build the Svelte frontend ----
FROM node:22-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# ---- 2. Build the Spring Boot jar with the frontend bundled as static resources ----
FROM eclipse-temurin:24-jdk AS backend
WORKDIR /app/backend
COPY backend/.mvn .mvn
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY backend/src src
COPY --from=frontend /app/frontend/dist src/main/resources/static
RUN ./mvnw -B -q package -DskipTests

# ---- 3. Slim runtime image ----
FROM eclipse-temurin:24-jre
WORKDIR /app
RUN useradd --system --no-create-home app
COPY --from=backend /app/backend/target/bodybuilding.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
