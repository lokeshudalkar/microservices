# ============================
# 🌟 Stage 1 — Build the app
# ============================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

# ============================
# 🚀 Stage 2 — Runtime image
# ============================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
EXPOSE 8082

COPY --from=build /app/target/*.jar job-service.jar

ENTRYPOINT ["java", "-jar", "job-service.jar"]
