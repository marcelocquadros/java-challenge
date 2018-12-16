# java-challenge
## The project is using the following tecnologies:
- SpringBoot 
- Lombok
- Spring Data JPA
- SpringBoot Test
- Java8 
- Maven
- Dockerfile maven plugin
# Running the project 
```sh
mvn clean package
mvn spring-boot:run 
```
# Running with docker
- Create the docker image
```sh
mvn clean package 
mvn dockerfile:build
```
- Running image created
```sh
docker run -p 8080:8080 marceloquadros/java-challenge
```
- Running 2 instances of the service
```sh
docker-compose up --scale java-challenge-api=2
```