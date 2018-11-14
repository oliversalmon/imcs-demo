#!/bin/bash
export KUBECONFIG=/etc/kubernetes/admin.conf
docker login -u dineshpillai -p Pill2017

getMyIP() {
    local _ip _myip _line _nl=$'\n'
    while IFS=$': \t' read -a _line ;do
        [ -z "${_line%inet}" ] &&
           _ip=${_line[${#_line[1]}>4?1:2]} &&
           [ "${_ip#127.0.0.1}" ] && _myip=$_ip
      done< <(LANG=C /sbin/ifconfig)
    printf ${1+-v} $1 "%s${_nl:0:$[${#1}>0?0:1]}" $_myip
}

getMyIP HOSTIPADDRESS
HBASECONTAINERID=docker ps -a | grep hbase | awk '{print $1}'

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

kubectl create namespace mu-architecture-demo

#Deploy to Kubernetes
cd ../kubernetes
kubectl apply -f run-mzk.yaml
hzformated=`cat "run-hz-jet-cluster.yaml" | sed -e 's/{{HOSTIPADDRESS}}/$HOSTIPADDRESS/g; s/{{HBASECONTAINERID}}/$HBASECONTAINERID/g'`
echo "$hzformated"|kubectl apply -f -
queryMicroservices=`cat "run-querymicroservices.yaml" | sed -e 's/{{HOSTIPADDRESS}}/$HOSTIPADDRESS/g; s/{{HBASECONTAINERID}}/$HBASECONTAINERID/g'`
echo "$queryMicroservices"|kubectl apply -f -
sleep 30s
./setKubeIP.sh
kubectl apply -f run-apps-reports-dep.yaml

kubectl apply -f jobmanager-controller.yaml
kubectl apply -f jobmanager-service.yaml
kubectl apply -f taskmanager-controller.yaml
