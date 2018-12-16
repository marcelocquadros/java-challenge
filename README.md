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
- Request
```sh 
curl -X POST \
  http://localhost:8080/conta-contabil \
  -H 'Content-Type: application/json' \
  -d '{
	"numero": 1010,
	 "descricao": "teste3"
}'
```
- Response
```sh
{"id":1010}
```
- Request
```sh
curl -X GET \
  http://localhost:8080/conta-contabil/1010
```
- Response
```sh
{"numero":1010,"descricao":"teste3"}
```
> /lancamentos-contabeis
- Request
```sh
curl -X POST \
  http://localhost:8080/lancamentos-contabeis \
  -H 'Content-Type: application/json' \
  -d '{
	"contaContabil": 1010,
	"data": 20171010,
	"valor": 1050.20
  }'  

```
- Response
```sh
{"id":"2c91808267b9120a0167b917f5780002"}
```
- Request
```sh
curl -X GET \
  http://localhost:8080/lancamentos-contabeis/2c91808267b9120a0167b917f5780002
```
- Response
```sh
{"contaContabil":1010,"data":20171010,"valor":1050.2}
```
- Request
```sh
curl -X GET \
  http://localhost:8080/lancamentos-contabeis 
```
- Response
```sh
[{"contaContabil":1010,"data":20171010,"valor":1050.2}]
```
- Request 
```sh
curl -X GET \
  http://localhost:8080/lancamentos-contabeis/_stats/?contaContabil=1010
```
- Response
```sh
{"soma":1050.2,"min":1050.2,"max":1050.2,"media":1050.2,"qtde":1}
```


