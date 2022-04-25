# Stuff to read

This is just a very small test service of https://github.com/JJDoneAway/meet-stackit. For further details please find its README

# Build

* test if application is running as expected `mvn clean spring-boot:run -Dspring-boot.run.profiles=dev` ==> http://localhost:8081/actuator/health
* build docker image `mvn spring-boot:build-image`
* test docker image `docker run -p 80:8080 meet-stackit-test:1` ==> http://localhost/actuator
* tag image as your global one. e.g.: `docker tag meet-stackit-test:1 hoehne/meet-stackit-test`
* push image to remote repo. e.g.: `docker push hoehne/meet-stackit-test`






