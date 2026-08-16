# =========================
# Build stage
# =========================

FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw -DskipTests clean package


# =========================
# Runtime stage
# =========================

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/careerhub-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]