# Dockerfile optimisé pour Render.com
# ── Stage 1 : Build ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Cache des dépendances séparé du code source
COPY pom.xml .
RUN mvn dependency:go-offline -q --no-transfer-progress 2>/dev/null || true

COPY src ./src
RUN mvn package -DskipTests -q --no-transfer-progress

# ── Stage 2 : Runtime ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S mouride && adduser -S mouride -G mouride

COPY --from=builder /app/target/*.jar app.jar
RUN chown mouride:mouride app.jar

# Script de démarrage qui convertit DATABASE_URL postgres:// → jdbc:postgresql://
COPY start.sh start.sh
RUN chmod +x start.sh && chown mouride:mouride start.sh

USER mouride
EXPOSE 8080

ENTRYPOINT ["./start.sh"]
