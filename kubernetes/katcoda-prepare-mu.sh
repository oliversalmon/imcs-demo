#!/bin/bash
if [ -z "$1" ]
  then
    echo "External Public IP not passed in"
    exit
fi


#Install Java and Maven
apt install software-properties-common -y
add-apt-repository ppa:webupd8team/java -y
apt-get update -y
apt-get install oracle-java8-installer -y

apt install maven -y

cd ../
docker login -u dineshpillai -p Pill2017

docker run -h hbasehost -d -p 2181:2181 -p 60000:60000 -p 60010:60010 -p 60020:60020 -p 60030:60030 -p 60040:60040 nerdammer/hbase

HOSTIPADDRESS=$1
HBASECONTAINERID=hbasehost

sed -i "s/{HOSTIPADDRESS}/$HOSTIPADDRESS/g; s/{HBASECONTAINERID}/$HBASECONTAINERID/g" ~/imcs-demo/database/src/main/java/com/example/mu/database/MuSchemaConstants.java

mvn clean package install -DskipTests


cd ./positionqueryservice
mvn docker:build
docker push dineshpillai/imcs-positionqueryservice

cd ../trade-injector
mvn docker:build
docker push dineshpillai/innovation-trade-injector

cd ../tradequerymicroservice
mvn docker:build
docker push dineshpillai/imcs-tradequeryservice



kubectl create namespace mu-architecture-demo
kubectl create clusterrolebinding default-admin --clusterrole cluster-admin --serviceaccount=default:mu-architecture-demo

cd ~/imcs-demo/kubernetes
kubectl apply -f run-mzk.yaml
hzformated=`cat "run-hz-jet-cluster.yaml" | sed -e "s/{{HOSTIPADDRESS}}/$HOSTIPADDRESS/g; s/{{HBASECONTAINERID}}/$HBASECONTAINERID/g"`
echo "$hzformated"|kubectl apply -f -
#queryMicroservices=`cat "run-querymicroservices.yaml" | sed -e "s/{{HOSTIPADDRESS}}/$HOSTIPADDRESS/g; s/{{HBASECONTAINERID}}/$HBASECONTAINERID/g"`
#echo "$queryMicroservices"|kubectl apply -f -

cd ~/imcs-demo/positionqueryservice
positionqueryformatted=`cat "manifests/position-query.yml" | sed -e "s/{{HOSTIPADDRESS}}/$HOSTIPADDRESS/g; s/{{HBASECONTAINERID}}/$HBASECONTAINERID/g"`
echo "$positionqueryformatted"|kubectl apply -f -

kubectl create -f manifests/position-query-config.yml


cd ~/imcs-demo/tradequerymicroservice
tradequerymicroserviceformatted=`cat "manifests/trade-query.yml" | sed -e "s/{{HOSTIPADDRESS}}/$HOSTIPADDRESS/g; s/{{HBASECONTAINERID}}/$HBASECONTAINERID/g"`
echo "$tradequerymicroserviceformatted"|kubectl apply -f -

kubectl create -f manifests/trade-query-config.yml

cd ~/imcs-demo/trade-injector
kubectl apply -f manifests/trade-injector.yml
kubectl apply -f manifests/trade-injector-configmap.yml



