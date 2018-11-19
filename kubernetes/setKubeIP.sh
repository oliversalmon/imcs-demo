#!/bin/sh
export KUBECONFIG=/etc/kubernetes/admin.conf

#first extract the postion query services  
POSITION_QUERY_SERVICE=$(kubectl get pods -n=mu-architecture-demo | grep position |  awk '{print $1}')
TRADE_QUERY_SERVICE=$(kubectl get pods -n=mu-architecture-demo | grep trade-query- |  awk '{print $1}')
cp ~/imcs-demo/kubernetes/run-apps-reports.yaml ~/imcs-demo/kubernetes/run-apps-reports-dep.yaml


#loop through each container id and replace host and ip with container id and ip address
counter=1
for i in $POSITION_QUERY_SERVICE
do
  export "IP=$(kubectl get pod ${i} --template={{.status.podIP}} -n=mu-architecture-demo)"
  sh -c  "echo \$IP"
  sed -i "s/IP_${counter}/${IP}/g" ~/imcs-demo/kubernetes/run-apps-reports-dep.yaml
  sed -i "s/HOST_${counter}/${i}/g" ~/imcs-demo/kubernetes/run-apps-reports-dep.yaml
  counter=$((counter+1))
done

counter=1
for i in $TRADE_QUERY_SERVICE
do
  export "IP=$(kubectl get pod ${i} --template={{.status.podIP}} -n=mu-architecture-demo)"
  sh -c  "echo \$IP"
  sed -i "s/IP_TR_${counter}/${IP}/g" ~/imcs-demo/kubernetes/run-apps-reports-dep.yaml
  sed -i "s/HOST_TR_${counter}/${i}/g" ~/imcs-demo/kubernetes/run-apps-reports-dep.yaml
  counter=$((counter+1))
done
