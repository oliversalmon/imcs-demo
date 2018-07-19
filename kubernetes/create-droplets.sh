TOKEN=fcc014835c1ab5eb7b67a91900bf7179f35e95f9083f63116c9ff99ec05982d8 

SSH_ID=`doctl compute ssh-key list | grep "Dinesh" | cut -d' ' -f1`

SSH_KEY=`doctl compute ssh-key get $SSH_ID --format FingerPrint --no-header`

doctl -t $TOKEN compute droplet create node-01 --size 2gb --image kube-master --region LON01 --ssh-keys  $SSH_KEY
