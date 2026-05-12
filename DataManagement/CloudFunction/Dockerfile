# Use the official Kotlin image from the Docker Hub
FROM openjdk:11-jre-slim

# Set the working directory
WORKDIR /app

# Copy the build artifacts
COPY build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
