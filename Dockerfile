FROM maven:3.8.3-openjdk-17
WORKDIR /app
COPY . /app/
RUN mvn clean package -DskipTests
EXPOSE 8081
CMD ["mvn", "spring-boot:run"]