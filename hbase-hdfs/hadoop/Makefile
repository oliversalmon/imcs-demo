all: hadoop-base hadoop-datanode hadoop-journalnode hadoop-namenode

.PHONY: hadoop-base hadoop-datanode hadoop-journalnode hadoop-namenode

IMAGE_BASE_URL=dineshpillai
IMAGE_TAG=2.7.7

hadoop-base:
	$(MAKE) -C hadoop-base

hadoop-datanode:
	$(MAKE) -C hadoop-datanode

hadoop-journalnode:
	$(MAKE) -C hadoop-journalnode

hadoop-namenode:
	$(MAKE) -C hadoop-namenode