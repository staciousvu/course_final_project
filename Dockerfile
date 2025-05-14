#FROM maven:3-openjdk-17 AS build
#WORKDIR /app
#
#COPY . .
#RUN mvn clean package -DskipTests
#
#
## Run stage
#
#FROM openjdk:17-jdk-slim
#WORKDIR /app
#
#COPY --from=build /app/target/DrComputer-0.0.1-SNAPSHOT.war drcomputer.war
#EXPOSE 8080
#
#ENTRYPOINT ["java","-jar","drcomputer.war"]
# Build stage
FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy WAR file from build stage
COPY --from=build /app/target/CourseApplicationProject-0.0.1-SNAPSHOT.war /app/courseapplicationproject.war

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/courseapplicationproject.war"]
