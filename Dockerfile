FROM openjdk:21-jdk
WORKDIR /app
COPY /build/libs/market-data-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9001  
CMD ["java", "-jar", "app.jar"]
