all: push clean

IMAGE_BASE_URL=dineshpillai
IMAGE_TAG=2.7.7

IMAGE_NAME=${IMAGE_BASE_URL}/hadoop-datanode
BASE_IMAGE=$(IMAGE_BASE_URL)/hadoop-base:${IMAGE_TAG}
addconf:
	cp -rf ../conf .

replace:
	sed -i '1 s|$$BASE_IMAGE|${BASE_IMAGE}|g' Dockerfile

build: addconf replace
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

push: build
    docker login -u dineshpillai -p Pill2017
	docker push $(IMAGE_NAME):$(IMAGE_TAG)

clean: addconf
	sed -i '1 s|.*|FROM $$BASE_IMAGE|g' Dockerfile
	rm -rf conf