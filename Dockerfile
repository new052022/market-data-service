FROM openjdk:21-jdk

WORKDIR /app


COPY target/*.jar app.jar 


EXPOSE 9001  # Replace with the actual port


CMD ["java", "-jar", "app.jar"]
