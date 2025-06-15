FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app

COPY . .

RUN gradle clean build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/partner-hub-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
