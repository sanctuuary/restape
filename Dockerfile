FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/restape-0.0.1.jar /app
ENTRYPOINT ["java", "-jar", "restape-0.0.1.jar"]
