if [ -z "$1" ]
  then
    echo "Build version number not provided"
    exit
fi

BUILDVERSION=$2

export KUBECONFIG=/etc/kubernetes/admin.conf

cd ~/imcs-demo/tradeInjectorFlink/

mvn clean install package -DskipTests -Pbuild-jar

cd ~/imcs-demo/tradeInjectorFlink/target

JOB_MANAGER_NAME=$(kubectl get pods -n=mu-architecture-demo | grep jobmanager |  awk '{print $1}')
echo $JOB_MANAGER_NAME
kubectl cp mu-flink-trade-injector-$BUILDVERSION.jar mu-architecture-demo/$JOB_MANAGER_NAME:/tmp/mu-flink-trade-injector-$BUILDVERSION.jar

#once copy is successful load the jar within the container
nohup kubectl exec -it $JOB_MANAGER_NAME -n=mu-architecture-demo ./bin/flink run /tmp/mu-flink-trade-injector-$BUILDVERSION.jar > /dev/null 2>&1 &

cd ~/imcs-demo/priceInjectorFlink/

mvn clean install package -DskipTests -Pbuild-jar

cd ~/imcs-demo/priceInjectorFlink/target

kubectl cp mu-flink-price-injector-$BUILDVERSION.jar mu-architecture-demo/$JOB_MANAGER_NAME:/tmp/mu-flink-price-injector-$BUILDVERSION.jar
nohup kubectl exec -it $JOB_MANAGER_NAME -n=mu-architecture-demo ./bin/flink run  /tmp/mu-flink-price-injector-$BUILDVERSION.jar > /dev/null 2>&1 &
