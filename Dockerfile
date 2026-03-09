# Build Stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN ./gradlew bootJar --no-daemon

# Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Use the dynamic PORT assigned by Render
ENTRYPOINT ["java", "-Xmx512m", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]
