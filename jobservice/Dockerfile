# ============================
# 🌟 Stage 1 — Build the app
# ============================
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Maven configuration first (for dependency caching)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download all dependencies to speed up future builds
RUN ./mvnw dependency:go-offline

# Copy the actual project source code
COPY src ./src

# Build the JAR file (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# ============================
# 🚀 Stage 2 — Runtime image
# ============================
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Expose the Job Service port
EXPOSE 8082

# Copy the built JAR from the build stage
COPY --from=build /app/target/job-service-0.0.1-SNAPSHOT.jar job-service.jar

# Set the default command to run the app
ENTRYPOINT ["java", "-jar", "job-service.jar"]
