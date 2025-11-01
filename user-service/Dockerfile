# Stage 1: The build stage (compiles your code)
# We use an official Maven image that includes Java 21, as specified in your pom.xml
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all dependencies. This is a separate step to leverage Docker's layer caching.
RUN ./mvnw dependency:go-offline

# Copy the rest of your source code
COPY src ./src

# Build the application and skip the tests
RUN ./mvnw clean package -DskipTests

# Stage 2: The final, lightweight runtime stage
# We use a minimal JRE image for a smaller, more secure container
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Expose the port your service runs on, as defined in your application.yml
EXPOSE 8081

# Copy the built JAR file from the 'build' stage
# The JAR name is based on your pom.xml <artifactId> and <version>
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar user-service.jar

# Set the command to run your application
ENTRYPOINT ["java", "-jar", "user-service.jar"]

