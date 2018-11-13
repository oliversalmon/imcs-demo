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
kubectl apply -f run-hz-jet-cluster.yaml
kubectl apply -f run-querymicroservices.yaml
sleep 30s
./setKubeIP.sh
kubectl apply -f run-apps-reports-dep.yaml

kubectl apply -f jobmanager-controller.yaml
kubectl apply -f jobmanager-service.yaml
kubectl apply -f taskmanager-controller.yaml
