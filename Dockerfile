FROM openjdk:17

EXPOSE 8080

RUN mkdir /app
RUN ls /home/

COPY ./build/libs/*.jar /app/spring-boot-application.jar

RUN mkdir -p ~/.postgresql
RUN curl "https://storage.yandexcloud.net/cloud-certs/CA.pem" -o ~/.postgresql/root.crt
RUN chmod 0600 ~/.postgresql/root.crt

ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]
