FROM gradle:7.2.0-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:17-jdk-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/carrent-backend.jar
CMD ["java", "-jar", "/app/carrent-backend.jar"]