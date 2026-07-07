FROM maven:3.9.9-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
COPY --from=build /target/group-0.0.1-SNAPSHOT.jar group.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "group.jar" ]
