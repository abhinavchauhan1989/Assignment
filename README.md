# Rabobank Customer Statement Processor
Rabobank receives monthly deliveries of customer statement records. This information is delivered in JSON Format.
These records need to be validated.

## Run the application
The application would be available on localhost at default port 8080.

For Running the application please use
mvn clean package
or
java -jar statement-validator-0.0.1-SNAPSHOT.jar

## Stack And Dependencies
 
 * Java 8
 * Spring Boot
 * JUnit
 * Unit and Integrated tests
 * Lombok
 * spring-boot-devtools

 ## Endpoints
 * POST http://localhost:8080/customer/payments
