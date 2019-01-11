all: push clean

IMAGE_NAME=dineshpillai/hbase-hdfs
IMAGE_TAG=latest

addconf:
	cp -rf ../hadoop/conf/*.xml .

build: addconf
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

push: build
	docker login -u dineshpillai -p Pill2017
	docker push $(IMAGE_NAME):$(IMAGE_TAG)

clean:
	rm core-site.xml hdfs-site.xml yarn-site.xml