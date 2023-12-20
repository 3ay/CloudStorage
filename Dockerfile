FROM openjdk:17-jdk-slim
ADD target/CloudStorage-0.0.1-SNAPSHOT.jar /cloud-storage_app.jar
EXPOSE 7070
ENTRYPOINT ["java","-jar","/cloud-storage_app.jar"]