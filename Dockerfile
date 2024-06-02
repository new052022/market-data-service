FROM openjdk:21-jdk

COPY /var/jenkins_home/workspace/monaco-market-service/0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]