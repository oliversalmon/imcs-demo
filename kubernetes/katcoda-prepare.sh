#Install Java and Maven
apt install software-properties-common -y
add-apt-repository ppa:webupd8team/java -y
apt-get update -y
apt-get install oracle-java8-installer -y

apt install maven -y

cd ../
docker login -u dineshpillai -p Pill2017

mvn clean package install -DskipTests


cd ./hello-service

docker build -t dineshpillai/hello-service .
docker push dineshpillai/hello-service

kubectl create clusterrolebinding default-admin --clusterrole cluster-admin --serviceaccount=default:default

kubectl create -f manifests/hello-service-configmap.yml
kubectl create -f manifests/hello-service.yml

