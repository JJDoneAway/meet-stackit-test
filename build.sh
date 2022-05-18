mvn spring-boot:build-image
docker tag meet-stackit-test:1 johanneshoehne1498/meet-stackit-test:$1
docker push johanneshoehne1498/meet-stackit-test:$1

