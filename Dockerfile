FROM openjdk:21-jdk

ARG JAR_FILE=build/libs/*.jar

COPY build/libs/0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]