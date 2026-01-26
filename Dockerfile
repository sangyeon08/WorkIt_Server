# Multi-stage build
FROM gradle:8.10-jdk17 AS builder
WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle/

# Copy source code
COPY src src/

# Build
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --no-daemon -x test

# Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8087

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]