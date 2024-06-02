FROM openjdk:21-jdk

COPY libs/market-data-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]