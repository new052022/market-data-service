FROM openjdk:21-jdk

WORKDIR /app

ENV POSTGRES_USER=postgres_user
ENV POSTGRES_PASS=postgres_pass
ENV DB_HOST=db_host
ENV SECRET_NUMBER=secret_number
ENV ALGORITHM=algo

COPY /build/libs/market-data-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 9001  


CMD ["java", "-jar", "app.jar"]
