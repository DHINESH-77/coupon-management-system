FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the JAR from your local build folder
COPY build/libs/coupon-management-system-0.0.1-SNAPSHOT.jar app.jar

# Important: Render uses a dynamic PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-Dserver.port=${PORT}", "-jar", "app.jar"]
