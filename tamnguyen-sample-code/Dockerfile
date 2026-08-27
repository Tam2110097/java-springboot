# FROM openjdk:17
FROM eclipse-temurin:17-jre

# ARG FILE_JAR=target/tamnguyen-sample-code-0.0.1-SNAPSHOT.jar

ARG FILE_JAR=target/*.jar

ADD ${FILE_JAR} api-service.jar

ENTRYPOINT ["java", "-jar", "api-service.jar"]

EXPOSE 80