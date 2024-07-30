FROM maven:3-eclipse-temurin-22 AS builder

WORKDIR /app

COPY pom.xml pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    mvn -f pom.xml dependency:resolve

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -f pom.xml clean package


FROM eclipse-temurin:22-jre-jammy 

WORKDIR /app
COPY --from=builder /app/target/*.jar /app/runner.jar

ENTRYPOINT ["java", "-jar", "/app/runner.jar"]
