FROM maven:3.6.3-jdk-17
COPY ./ ./
RUN mvn clean package
CMD ["java", "-jar","target/translator-0.0.1-SNAPSHOT.jar"]