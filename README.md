***Demo Implementation of the Mu Architecture***

Please the [wiki](https://github.com/oliversalmon/imcc-demo/wiki) for an overview of the architecture

***Installing and Running the architecture***

**Create Droplets from Digitial Ocean**

* Create a minimum of 2-3 droplets
* Call the master mu-master-01 and the slaves mu-slave-01 
and so on
* The master must have biggger diskspace as it will be used
to run HBase and hold all the data

**Install Git and download repository**

* Install Git to each master and slave by running the following commands

```
apt-get update
apt-get install git-core
```

* Download Repository
```$xslt
git clone https://github.com/oliversalmon/imcs-demo.git
```

**Install mu in each droplet**

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

**Deploy the architecture**