FROM openjdk:17-jdk-alpine
COPY xzn-project-backend/target/xzn-project-backend-0.0.1-SNAPSHOT.jar /work/app.jar
WORKDIR /work
CMD ["java", "-jar", "app.jar"]
#docker run -d -p 8081:8081 --name back --restart always forum-backend