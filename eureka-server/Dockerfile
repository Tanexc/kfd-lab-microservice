FROM gradle:jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17
EXPOSE 8761:8761
RUN mkdir /eureka
COPY --from=build /home/gradle/src/build/libs/*.jar /eureka/eureka.jar
ENTRYPOINT ["java","-jar","/eureka/eureka.jar"]