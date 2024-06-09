FROM openjdk:21-jdk

WORKDIR /app

# Copy the JAR file from the context (assuming it's built outside the container)
COPY target/*.jar app.jar  # Adjust the source path if necessary

# Expose the port your Spring Boot application listens on (optional)
EXPOSE 9001  # Replace with the actual port

# Entrypoint command to execute the application
CMD ["java", "-jar", "app.jar"]
