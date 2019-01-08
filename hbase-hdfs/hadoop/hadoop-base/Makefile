all: push

IMAGE_NAME=dineshpillai/hadoop-base

build:
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

push: build
	docker login -u dineshpillai -p Pill2017
	docker push $(IMAGE_NAME):$(IMAGE_TAG)