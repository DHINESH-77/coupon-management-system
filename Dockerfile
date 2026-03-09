# Step 1: Build the JAR on Render
FROM gradle:7.6-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Step 2: Run the JAR
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Dynamic PORT for Render
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-Dserver.port=${PORT}", "-jar", "app.jar"]
