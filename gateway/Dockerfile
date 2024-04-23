FROM gradle:jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17
EXPOSE 8765:8765
RUN mkdir /gateway
COPY --from=build /home/gradle/src/build/libs/*.jar /gateway/gateway.jar
ENTRYPOINT ["java","-jar","/gateway/gateway.jar"]