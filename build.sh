mvn spring-boot:build-image
docker tag meet-stackit-test:1 hoehne/meet-stackit-test:$1
docker push hoehne/meet-stackit-test:$1

