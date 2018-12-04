#Install Java and Maven
add-apt-repository ppa:webupd8team/java
apt-get update -y
apt-get install oracle-java8-installer -y

apt install maven -y

cd imcs-demo
docker login -u dineshpillai -p Pill2017

mvn clean package install -DskipTests

cd ./pricequeryservice

mvn docker:build
docker push dineshpillai/mu-pricequery-service
