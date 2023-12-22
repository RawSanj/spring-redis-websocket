FROM maven:3.8.8-eclipse-temurin-17 as build
WORKDIR app
COPY . .
RUN mvn clean package --file pom.xml

FROM eclipse-temurin:17-jdk-jammy
COPY --from=build target/spring-redis-websocket-3.2.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
