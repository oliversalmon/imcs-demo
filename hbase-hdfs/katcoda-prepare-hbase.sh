#!/bin/bash

#Install Java and Maven
apt install software-properties-common -y
add-apt-repository ppa:webupd8team/java -y
apt-get update -y
apt-get install oracle-java8-installer -y

apt install maven -y

HOSTIPADDRESS=zoo1
HBASECONTAINERID=hbase-master-a

sed -i "s/{HOSTIPADDRESS}/$HOSTIPADDRESS/g; s/{HBASECONTAINERID}/$HBASECONTAINERID/g" ~/imcs-demo/database/src/main/java/com/example/mu/database/MuSchemaConstants.java

#Create the docker images
docker login -u dineshpillai -p Pill2017

cd ~/imcs-demo/hbase-hdfs/hadoop
make

cd ~/imcs-demo/hbase-hdfs/hbase
make

#Apply the kube configs
kubectl create namespace mu-architecture-demo
kubectl create clusterrolebinding default-admin --clusterrole cluster-admin --serviceaccount=mu-architecture-demo:default

cd ~/imcs-demo/kubernetes
kubectl apply -f run-mzk.yaml


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


cd ~/imcs-demo
mvn clean package install -DskipTests

cd ~/imcs-demo/database
mvn docker:build
docker push dineshpillai/innovation-mu-database-utility


kubectl create -f yaml/database-connect.yaml



