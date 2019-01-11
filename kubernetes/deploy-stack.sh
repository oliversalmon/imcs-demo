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


#Build hbase,hadoop-base, hadoop-journal, hadoop-namenode

d ~/imcs-demo/kubernetes
kubectl apply -f run-mzk.yaml

cd ~/imcs-demo/hbase-hdfs/hadoop
make
cd ~/imcs-demo/hbase-hdfs/hbase
make

#Create the namespace and provide default admin access to all pods and services under this namespace
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

#Do all the builds, create the containers and push while hbase cluster is spinning up
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

#Create the tables in hbase
cd ~/imcs-demo/database
kubectl create -f yaml/database-connect.yaml
sleep 30s

#Quit if it completes
while $DB_UTILITY_RUNNING='Running'
do
    DB_UTILITY_RUNNING=$(kubectl get pods -n=mu-architecture-demo | grep database-utility |  awk '{print $3}')
    echo $DB_UTILITY_RUNNING
    sleep 10s
done

#Deploy the rest of the stack to Kubernetes
cd ~/imcs-demo/kubernetes
kubectl apply -f run-hz-jet-cluster.yaml

cd ~/imcs-demo/positionqueryservice
kubectl apply -f  manifests/position-query.yaml
kubectl create -f manifests/position-query-config.yml

cd ~/imcs-demo/tradequerymicroservice
kubectl apply -f - manifests/trade-query.yml
kubectl create -f manifests/trade-query-config.yml

cd ~/imcs-demo/trade-injector
kubectl apply -f manifests/trade-injector.yml
kubectl apply -f manifests/trade-injector-configmap.yml


cd ~/imcs-demo/kubernetes
kubectl apply -f jobmanager-controller.yaml
kubectl apply -f jobmanager-service.yaml
kubectl apply -f taskmanager-controller.yaml
