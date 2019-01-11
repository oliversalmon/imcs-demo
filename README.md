# Demo Implementation of the Mu Architecture 

Please the [wiki](https://github.com/oliversalmon/imcc-demo/wiki) for an overview of the architecture

# Installing and Running the architecture

## Create Droplets from Digitial Ocean

* Create a minimum of 2-3 droplets
* Call the master mu-master-01 and the slaves mu-slave-01 
and so on
* The master must have biggger diskspace as it will be used
to run HBase and hold all the data

Note: Minimum requirement to run the architecture

* Master - 4GB Memory
* Slaves - 16GB Memory (collectively)

## Install Git and download repository

* Install Git to each master and slave by running the following commands

```
apt-get update
apt-get install git-core
```

* Download Repository and checkout the tagged releasable version
```
git clone https://github.com/oliversalmon/imcs-demo.git
cd ~/imcs-demo
git checkout v1.8
```

In the above example the tagged version is 1.8

## Install Kubernetes and Run in each droplet

Run the following shell script to install the following components
* Java (Oracle JDK 8)
* Maven
* Docker
* Kubeadm
* Kubectl
* Kubelet

```
cd ~/imcs-demo/kubernetes/

./create-master-slave.sh
```

You will be prompted to enter Y/n while it is installing certain components; please enter Y.

**Start up Kubeadm in master**

Run the following shell script to run Kube admin and install Hbase

```
./initialise-master.sh
```
Note: Please take a note of kubeadm join instructions; this will appear as such. The IP and hash token will change and the below
is only an example taken from the current installation

```
kubeadm join 209.97.138.77:6443 --token rj8wm6.3mhdj99v5akgoxvx --discovery-token-ca-cert-hash sha256:e822f0e646d187552d2edba7ac53e70b82da14593b9cda18da535f4757c0b948
``` 

**Join kube slaves to master**

Run the following join command on each slave for each slave to join
successfully to the master

```
kubeadm join 209.97.138.77:6443 --token rj8wm6.3mhdj99v5akgoxvx --discovery-token-ca-cert-hash sha256:e822f0e646d187552d2edba7ac53e70b82da14593b9cda18da535f4757c0b948
``` 


## Deploy and run the architecture

The following command will deploy the following components
on the kubernetes cloud

* Zookeeper
* Kafka (for Market Data and Trade Data)
* MongoDB (to register state of play with Trade Injector)
* Trade IMDB - In Memory Data Grid
* Trade Query Service - To query the Trade Cache
* Position Query Service - To query positions of Position Cache
* Trade Injector - Injecting and Reporting UI
* Flink Cluster and Job Manager to stream and manage state of Trades and Positions

```
./deploy-stack.sh  <deployable tagged version number>
```
Note: The tag version will be the number without 'v'. For Eg v1.8 will be 1.8

**Confirm if the applications run successfully**

The following commands will verify if all the relevant components are running

```
export KUBECONFIG=/etc/kubernetes/admin.conf

kubectl get pods -n=mu-architecture-demo

NAME                                 READY     STATUS      RESTARTS   AGE
database-utility                     0/1       Completed   0          10m
db                                   1/1       Running     0          24m
flink-jobmanager-547ffbc9dc-hqtst    1/1       Running     0          10m
flink-taskmanager-6bd5d97489-26m8w   1/1       Running     0          10m
flink-taskmanager-6bd5d97489-592mr   1/1       Running     0          10m
flink-taskmanager-6bd5d97489-6bwq7   1/1       Running     0          10m
flink-taskmanager-6bd5d97489-b22t9   1/1       Running     0          10m
hadoop-datanode-1                    1/1       Running     0          17m
hadoop-datanode-2                    1/1       Running     0          17m
hadoop-journalnode-0                 1/1       Running     0          18m
hadoop-journalnode-1                 1/1       Running     0          18m
hadoop-journalnode-2                 1/1       Running     0          18m
hadoop-namenode-0                    1/1       Running     0          18m
hadoop-namenode-1                    1/1       Running     1          18m
hbase-master-a                       1/1       Running     0          17m
hbase-region-a                       1/1       Running     0          17m
hbase-region-b                       1/1       Running     0          17m
kafka-broker-d47bk                   1/1       Running     1          24m
position-query-6449587cd4-rrgv4      1/1       Running     0          10m
trade-imdg-689cc8fcf8-zx2wr          1/1       Running     0          10m
trade-injector-7f8d7c57f7-gmwkw      1/1       Running     0          10m
trade-query-6d4b566f95-6qq8v         1/1       Running     0          10m


```
**Install the Trade and Price Flink jars**

Once the flink cluster is confirmed running. 
Run the following command from the kubernetes directory


```
cd ~/imcs-demo/kubernetes

./loadjarsToFlink.sh <deployable git release tag>

```

Note: The release tag will be the git release tag without the 'v'. For eg: v1.8 will be 1.8

**Run the UI and set up login credentials with FB**

To login into the UI; you will need the port the Injector UI runs on. Run the following

```
kubectl get svc -n=mu-architecture-demo

NAME                     TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                                        AGE
db                       ClusterIP   10.110.216.169   <none>        27017/TCP                                      40m
flink-jobmanager         ClusterIP   10.104.186.26    <none>        6123/TCP,6124/TCP,6125/TCP,8081/TCP            39m
kafka                    ClusterIP   10.105.185.120   <none>        9092/TCP                                       40m
position-query-service   NodePort    10.100.121.27    <none>        8093:31633/TCP                                 40m
trade-imdg-service       ClusterIP   10.104.51.125    <none>        5701/TCP                                       40m
trade-injector-service   NodePort    10.100.45.79     <none>        8090:31891/TCP                                 39m
trade-query-service      NodePort    10.103.202.20    <none>        8094:31111/TCP                                 40m
zoo1                     NodePort    10.101.90.222    <none>        2181:32021/TCP,2888:32076/TCP,3888:30751/TCP   40m
```

Take a note of the ports used in trade-injector-service. Ignore 8090 and use the port number to the left of it. In the
above example it is 31891

Login to Trade Injector UI with the following URL

```
http://<public ip of master DO>:<port number obtained from the above step>
```

You can now start to generate test trades and view the position reports.

You have successfully deployed and have the application running at this point.

**Monitoring the architecture**

*Monitoring Logs*

To monitor a log from the container; we can run the following commands

```
$ export KUBECONFIG=/etc/kubernetes/admin.conf

$ kubectl get pods -n=mu-architecture-demo

NAME                                 READY     STATUS      RESTARTS   AGE
database-utility                     0/1       Completed   0          10m
db                                   1/1       Running     0          24m
flink-jobmanager-547ffbc9dc-hqtst    1/1       Running     0          10m
flink-taskmanager-6bd5d97489-26m8w   1/1       Running     0          10m
flink-taskmanager-6bd5d97489-592mr   1/1       Running     0          10m
flink-taskmanager-6bd5d97489-6bwq7   1/1       Running     0          10m
flink-taskmanager-6bd5d97489-b22t9   1/1       Running     0          10m
hadoop-datanode-1                    1/1       Running     0          17m
hadoop-datanode-2                    1/1       Running     0          17m
hadoop-journalnode-0                 1/1       Running     0          18m
hadoop-journalnode-1                 1/1       Running     0          18m
hadoop-journalnode-2                 1/1       Running     0          18m
hadoop-namenode-0                    1/1       Running     0          18m
hadoop-namenode-1                    1/1       Running     1          18m
hbase-master-a                       1/1       Running     0          17m
hbase-region-a                       1/1       Running     0          17m
hbase-region-b                       1/1       Running     0          17m
kafka-broker-d47bk                   1/1       Running     1          24m
position-query-6449587cd4-rrgv4      1/1       Running     0          10m
trade-imdg-689cc8fcf8-zx2wr          1/1       Running     0          10m
trade-injector-7f8d7c57f7-gmwkw      1/1       Running     0          10m
trade-query-6d4b566f95-6qq8v         1/1       Running     0          10m

$ kubectl logs -f kafka-broker-d47bk  -n=mu-architecture-demo

```
The last line will tail the logs for the kafka broker

*E2E Testing*

Test end to end by injecting trades into Kafka topics. This can be done with a simple utility called kafkacat

```
$ apt-get install kafkacat
```

Launch a seperate shell to run the consumer

* First obtain the kafka IP.
In the example below it is 10.105.185.120

```
export KUBECONFIG=/etc/kubernetes/admin.conf

kubectl get svc -n=mu-architecture-demo

NAME                     TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                                        AGE
db                       ClusterIP   10.110.216.169   <none>        27017/TCP                                      40m
flink-jobmanager         ClusterIP   10.104.186.26    <none>        6123/TCP,6124/TCP,6125/TCP,8081/TCP            39m
kafka                    ClusterIP   10.105.185.120   <none>        9092/TCP                                       40m
position-query-service   NodePort    10.100.121.27    <none>        8093:31633/TCP                                 40m
trade-imdg-service       ClusterIP   10.104.51.125    <none>        5701/TCP                                       40m
trade-injector-service   NodePort    10.100.45.79     <none>        8090:31435/TCP                                 39m
trade-query-service      NodePort    10.103.202.20    <none>        8094:31111/TCP                                 40m
zoo1                     NodePort    10.101.90.222    <none>        2181:32021/TCP,2888:32076/TCP,3888:30751/TCP   40m

```

* Next run the consumer against the trade topic
```
kafkacat -b 10.105.185.120:9092 -t trade
```

* Send a dummy message and see the consumer above consume the message

```
$ cat dummy.txt | kafkacat -b 10.105.185.120 -t trade
```

* If the above test was successful; you can repeat the test for market_data topic