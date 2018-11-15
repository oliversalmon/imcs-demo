kubeadm init
export KUBECONFIG=/etc/kubernetes/admin.conf

sysctl net.bridge.bridge-nf-call-iptables=1
kubectl apply -f "https://cloud.weave.works/k8s/net?k8s-version=$(kubectl version | base64 | tr -d '\n')"


#run the hbase container
docker run -h hbasehost -d -p 2181:2181 -p 60000:60000 -p 60010:60010 -p 60020:60020 -p 60030:60030 -p 60040:60040 nerdammer/hbase
