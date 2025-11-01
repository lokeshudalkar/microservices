# ---------- Stage 1: Build Stage ----------
# Use an official Maven image with Java 21 to build the project
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy only pom.xml and wrapper first (for caching dependencies)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Build the project (skip tests to speed up)
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Runtime Stage ----------
# Use lightweight JRE for final image
FROM eclipse-temurin:21-jre-jammy

# Set working directory inside container
WORKDIR /app

# Expose your service port (change if your Application Service uses a different port)
EXPOSE 8083

# Copy the built JAR file from build stage
COPY --from=build /app/target/application-service-0.0.1-SNAPSHOT.jar application-service.jar

# Command to run the app
ENTRYPOINT ["java", "-jar", "application-service.jar"]
