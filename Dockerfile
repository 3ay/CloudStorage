FROM openjdk:17-jdk-slim
ADD target ./target
EXPOSE 7070
ENTRYPOINT ["java","-jar","target/CloudStorage-0.0.1-SNAPSHOT.jar"]