# This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software Project course.
# © Copyright Utrecht University (Department of Information and Computing Sciences)
FROM maven:3.6.3-adoptopenjdk-11
WORKDIR /app
COPY pom.xml /app
RUN mvn compile
COPY . /app
RUN mvn package -DskipTests=true -P docker
ENTRYPOINT ["java", "-jar", "target/restape-0.0.1.jar"]
