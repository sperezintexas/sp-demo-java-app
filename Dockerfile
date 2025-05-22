# Use a multi-stage build to keep the final image small
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Copy the source code
COPY src src

# Build the application
RUN ./gradlew build -x test

# Runtime stage: Run the app
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Install wget and unzip, then check if sealights folder exists, if not download and extract it
RUN apt-get update && apt-get install -y wget unzip && \
    if [ ! -d "sealights" ]; then \
        wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip && \
        unzip -oq sealights-java-latest.zip -d sealights; \
    fi

# Expose the port the app runs on
EXPOSE 8080

# Run the application (args overridden in Kubernetes)
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
