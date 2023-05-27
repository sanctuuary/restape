FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/restape-0.2.0.jar /app
ENTRYPOINT ["java", "-jar", "restape-0.2.0.jar"]
