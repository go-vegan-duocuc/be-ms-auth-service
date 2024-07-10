#Dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS buildstage

WORKDIR /ms-auth-service

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:17 AS runstage

WORKDIR /app

COPY --from=buildstage /ms-auth-service/target/ms-auth-service-0.0.1-SNAPSHOT.jar ms-auth-service.jar

ENTRYPOINT ["java", "-jar", "ms-auth-service.jar"]