FROM alpine:latest
EXPOSE 8000
RUN apk add --no-cache openjdk17
COPY target/nss_cron_ms-0.0.1-SNAPSHOT.jar /app_cron.jar

CMD ["java", "-jar", "/app_cron.jar"]