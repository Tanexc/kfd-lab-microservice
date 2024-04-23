FROM gradle:jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17
EXPOSE 8001:8001
RUN mkdir /roomservice
COPY --from=build /home/gradle/src/build/libs/*.jar /roomservice/roomservice.jar
ENTRYPOINT ["java","-jar","/roomservice/roomservice.jar"]