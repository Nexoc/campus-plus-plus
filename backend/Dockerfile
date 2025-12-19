# ==================================================
# BUILD STAGE
# ==================================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy Maven configuration first (for better layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd

# Make Maven Wrapper executable (required for Linux CI environments)
RUN chmod +x mvnw

# Download dependencies only
RUN ./mvnw dependency:go-offline

# Copy application source code
COPY src src

# Build the application JAR
RUN ./mvnw clean package -DskipTests


# ==================================================
# RUNTIME STAGE
# ==================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /build/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
