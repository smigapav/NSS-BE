FROM alpine:latest
EXPOSE 8080
RUN apk add --no-cache openjdk17
COPY target/reservation_system-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]