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

- Running 2 instances of the service without a load balancer
```sh
 docker run -p 8080:8080 marceloquadros/java-challenge
 docker run -p 8081:8080 marceloquadros/java-challenge
```

# Testing the endpoints
> /conta-contabil
```sh 
curl -X POST \
  http://localhost:8080/conta-contabil \
  -H 'Content-Type: application/json' \
  -d '{
	"numero": 1010,
	 "descricao": "teste3"
}
```
```sh
curl -X GET \
  http://localhost:8080/conta-contabil/1000
```
> /lancamentos-contabeis
```sh
curl -X POST \
  http://localhost:8080/lancamentos-contabeis \
  -H 'Content-Type: application/json' \

  -d '{
	"contaContabil": 1010,
	"data": 20171010,
	"valor": 50.38909090
  }'  

```
- Note: Replace the value below (377ffc78-a85f-4705-8480-92c04136fa73) by the id in previous response
```sh
curl -X GET \
  http://localhost:8080/lancamentos-contabeis/377ffc78-a85f-4705-8480-92c04136fa73
    
```
```sh
curl -X GET \
  http://localhost:8080/lancamentos-contabeis 
```
```sh
curl -X GET \
  http://localhost:8080/lancamentos-contabeis/_stats/?contaContabil=1010
```
