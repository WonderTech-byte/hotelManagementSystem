# Multi-stage build to optimize image size
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage with minimal footprint
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Expose API port
EXPOSE 8080

# Health check for container orchestration
HEALTHCHECK --interval=30s --timeout=10s --retries=5 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment variables documentation
ENV SPRING_DATASOURCE_URL="jdbc:h2:./hotel_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE" \
    SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.h2.Driver" \
    SPRING_DATASOURCE_USERNAME="sa" \
    SPRING_DATASOURCE_PASSWORD="" \
    SPRING_JPA_HIBERNATE_DDL_AUTO="update" \
    SPRING_JPA_SHOW_SQL="false" \
    SPRING_H2_CONSOLE_ENABLED="true"

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
