FROM openjdk:11-jdk-oracle
VOLUME /tmp
ARG JAR_FILE=consumer-service-0.0.1-SNAPSHOT.jar
COPY ./target/${JAR_FILE} app.jar
ENV TZ=Europe/Rome
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
