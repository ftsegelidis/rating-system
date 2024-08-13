# Rating service
This is the skeleton for a simple rating service created with Springboot.


## Prerequisites 

1. Jdk 15
2. maven 3.8+
3. docker
4. postman


## Installation

- mvn clean install 

## How to run

#### Profile : local  

```
 mvn spring-boot:run -Dspring-boot.run.profiles=local
 ```
 in-memory database : h2 
 initial data (based on the example) : data.sql

#### Profile : production
```
  docker-compose up --build
```
 Consult Dockerfile and docker-compose.yml



## How to test it

1. Open postman
2. Make a get request with url 
```
http://localhost:8080/ratings?rated_entity=property_3742&specificDate=2020/11/04 
```

the specificDate is an extra optional parameter, if it's absent then the service will perform calculation based on the current date

3. Make a post request with the following request body : 

```
http://localhost:8080/ratings

{
"givenRating" : "5.0",
"ratedEntity" : "xe",
"rater" : null
}
```

4. Confirm that the rating created :
```
http://localhost:8080/ratings?rated_entity=xe
```

## Bonus




## Extra

Dependencies :
```
For swagger2
		<dependency>
			<groupId>io.springfox</groupId>
			<artifactId>springfox-swagger2</artifactId>
			<version>2.4.0</version>
		</dependency>
		<dependency>
			<groupId>io.springfox</groupId>
			<artifactId>springfox-swagger-ui</artifactId>
			<version>2.4.0</version>
		</dependency>
		
For health check		
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

For in-memory database
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>

```
