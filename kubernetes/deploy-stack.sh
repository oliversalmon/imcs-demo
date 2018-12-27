#!/bin/bash

if [ -z "$1" ]
  then
    echo "External Public IP not passed in"
    exit
fi

export KUBECONFIG=/etc/kubernetes/admin.conf
docker login -u dineshpillai -p Pill2017

HOSTIPADDRESS=$1
HBASECONTAINERID=hbasehost



#Bake the Hbase Host and Container Id in MuSchema constants
sed -i "s/{HOSTIPADDRESS}/$HOSTIPADDRESS/g; s/{HBASECONTAINERID}/$HBASECONTAINERID/g" ~/imcs-demo/database/src/main/java/com/example/mu/database/MuSchemaConstants.java

#Connect up to Hbase to create the tables and schema

#Do all the builds, create the containers and push
cd ../
mvn clean package install -DskipTests
cd ./trade-imdg
mvn docker:build
docker push dineshpillai/innovation-trade-imdg
cd ../trade-injector
mvn docker:build
docker push dineshpillai/innovation-trade-injector
cd ../tradequerymicroservice
mvn docker:build
docker push dineshpillai/imcs-tradequeryservice
cd ../positionqueryservice
mvn docker:build
docker push dineshpillai/imcs-positionqueryservice

#Connect up to Hbase to create the tables and schema
echo "$HOSTIPADDRESS hbasehost" >> /etc/hosts
cd ~/imcs-demo/database/target
java -jar database-1.1.jar hbasehost=$HOSTIPADDRESS zkhost=$HOSTIPADDRESS

kubectl create namespace mu-architecture-demo
kubectl create clusterrolebinding default-admin --clusterrole cluster-admin --serviceaccount=default:mu-architecture-demo

#Deploy to Kubernetes
cd ~/imcs-demo/kubernetes
kubectl apply -f run-mzk.yaml
hzformated=`cat "run-hz-jet-cluster.yaml" | sed -e "s/{{HOSTIPADDRESS}}/$HOSTIPADDRESS/g; s/{{HBASECONTAINERID}}/$HBASECONTAINERID/g"`
echo "$hzformated"|kubectl apply -f -

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

cd ~/imcs-demo/kubernetes
kubectl apply -f jobmanager-controller.yaml
kubectl apply -f jobmanager-service.yaml
kubectl apply -f taskmanager-controller.yaml
