FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM alpine:latest
EXPOSE 8000
RUN apk add --no-cache openjdk17
COPY --from=build /app/target/nss_cron_ms-1.0.jar /app.jar

CMD ["java", "-jar", "/app.jar"]