FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring -g 1000 && adduser -S spring -u 1000 -G spring

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app/application.jar
RUN chown spring:spring /app/application.jar

USER spring:spring
WORKDIR /app

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/application.jar"]
