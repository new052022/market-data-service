FROM openjdk:21-jdk

COPY /var/jenkins_home/workspace/monaco-market-service app.jar

CMD ["java", "-jar", "app.jar"]