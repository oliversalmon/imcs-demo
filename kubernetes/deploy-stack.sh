#!/bin/bash


if [ -z "$1" ]
  then
    echo "Build version number not provided"
    exit
fi

BUILDVERSION=$1
git checkout $BUILDVERSION

export KUBECONFIG=/etc/kubernetes/admin.conf
docker login -u dineshpillai -p Pill2017

HOSTIPADDRESS=zoo1
HBASECONTAINERID=hbase-master-a



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
cd ~/imcs-demo/database
mvn docker:build
docker push dineshpillai/innovation-mu-database-utility

#Build hbase,hadoop-base, hadoop-journal, hadoop-namenode
cd ~/imcs-demo/hbase-hdfs/hadoop
make
cd ~/imcs-demo/hbase-hdfs/hbase
make

#Connect up to Hbase to create the tables and schema
#echo "$HOSTIPADDRESS hbasehost" >> /etc/hosts
#cd ~/imcs-demo/database/target
#java -jar database-$BUILDVERSION.jar hbasehost=$HOSTIPADDRESS zkhost=$HOSTIPADDRESS

kubectl create namespace mu-architecture-demo
kubectl create clusterrolebinding default-admin --clusterrole cluster-admin --serviceaccount=mu-architecture-demo:default

#Deploy hbase, hadoop cluster
cd ~/imcs-demo/hbase-hdfs/hadoop
kubectl create -f yaml/journalnode.yaml
sleep 10s


kubectl create -f yaml/namenode0.yaml
kubectl create -f yaml/namenode1.yaml
sleep 30s

kubectl create -f yaml/datanode.yaml
sleep 60s

cd ~/imcs-demo/hbase-hdfs
kubectl create -f hmaster.yaml
kubectl create -f region.yaml

#Deploy the rest of the stack to Kubernetes
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

cd ~/imcs-demo/database
kubectl create -f yaml/database-connect.yaml

cd ~/imcs-demo/kubernetes
kubectl apply -f jobmanager-controller.yaml
kubectl apply -f jobmanager-service.yaml
kubectl apply -f taskmanager-controller.yaml
