all: push

IMAGE_BASE_URL=dineshpillai
IMAGE_TAG=2.7.7
IMAGE_NAME=dineshpillai/hadoop-base

build:
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) .

push: build
	docker login -u dineshpillai -p Pill2017
	docker push $(IMAGE_NAME):$(IMAGE_TAG)