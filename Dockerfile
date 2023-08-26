FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY restape-0.2.4.jar /app
ENTRYPOINT ["java", "-jar", "restape-0.2.4.jar"]
