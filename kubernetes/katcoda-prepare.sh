#Install Java and Maven
apt install software-properties-common -y
add-apt-repository ppa:webupd8team/java -y
apt-get update -y
apt-get install oracle-java8-installer -y

apt install maven -y

cd ../
docker login -u dineshpillai -p Pill2017

mvn clean package install -DskipTests


cd ./pricequeryservice

docker build -t dineshpillai/mu-pricequery-service .
docker push dineshpillai/mu-pricequery-service

kubectl create clusterrolebinding default-admin --clusterrole cluster-admin --serviceaccount=default:default

kubectl apply -f ./price-query-configmap.yml
kubectl apply -f ./price-service.yaml
