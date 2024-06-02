FROM openjdk:21-jdk
WORKDIR /app
COPY var/jenkins_home/monaco-market-service/build/libs/market-data-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]