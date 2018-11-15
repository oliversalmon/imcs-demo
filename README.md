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
* Slaves - 8GB Memory (collectively)

## Install Git and download repository

* Install Git to each master and slave by running the following commands

```
apt-get update
apt-get install git-core
```

* Download Repository
```
git clone https://github.com/oliversalmon/imcs-demo.git
```

## Install Kubernetes and Run in each droplet

Run the following shell script to install the following components
* Java (Oracle JDK 8)
* Maven
* Docker
* Kubeadm
* Kubectl
* Kubelet

```
cd imcs-demo/kubernetes/
chmod +x *.sh
./create-master-slave.sh
```

You will be prompted to enter Y/n while it is installing components; please enter Y.

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
./deploy-stack.sh <Public IP address of master>
```
Note: The public IP address will be obtained from the Digital Ocean console 

**Confirm if the applications run successfully**

The following commands will verify if all the relevant components are running

```
export KUBECONFIG=/etc/kubernetes/admin.conf

kubectl get pods -n=mu-architecture-demo

NAME                                         READY   STATUS             RESTARTS   AGE
db                                           1/1     Running            0          39m
flink-jobmanager-676d7f5fb5-kwsg4            1/1     Running            0          38m
flink-taskmanager-9bf7c94bc-9tcf7            1/1     Running            0          38m
flink-taskmanager-9bf7c94bc-jlmdt            1/1     Running            0          38m
flink-taskmanager-9bf7c94bc-vzpt2            1/1     Running            0          38m
flink-taskmanager-9bf7c94bc-zz952            1/1     Running            0          38m
kafka-broker-bnq7g                           1/1     Running            0          39m
position-query-684b6cf7cd-7dx4q              1/1     Running            0          39m
position-query-684b6cf7cd-w5fnz              1/1     Running            0          39m
trade-imdg-7789965756-snl79                  1/1     Running            0          39m
trade-injector-controller-7bc6fdd8d6-t5vq8   1/1     Running            0          38m
trade-query-5f964dd46d-h7bx8                 0/1     CrashLoopBackOff   10         39m
zookeeper-controller-1-rjcn2                 1/1     Running            0          39m

```
**Install the Trade and Price Flink jars**

Once the flink cluster is confirmed running. 
Run the following command from the kubernetes directory


```
cd ~/imcs-demo/kubernetes

./loadjarsToFlink.sh

```


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
trade-injector-service   NodePort    10.100.45.79     <none>        8090:31435/TCP                                 39m
trade-query-service      NodePort    10.103.202.20    <none>        8094:31111/TCP                                 40m
zoo1                     NodePort    10.101.90.222    <none>        2181:32021/TCP,2888:32076/TCP,3888:30751/TCP   40m
```

Take a note of the ports used in trade-injector-service. Ignore 8090 and use the port number to the left of it. In the
above example it is 31435

Login to Trade Injector UI with the following URL

```
http://<public ip of master DO>:<port number obtained from the above step>
```

You will see the Login page of Trade Injector UI showing a Facebook or Github login. Login with facebook.
Before you login; you will need to register the above URL in facebook/developer. Please speak to the authors of this 
application to register the URL

Once you have logged in; you can start to generate test trades and view the position reports.

You have successfully deployed and have the application running at this point.

**Monitoring the architecture**