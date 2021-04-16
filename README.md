# kafka-stream-processing
Kafka Stream Processing

# Prerequisite
- Docker (https://www.docker.com/products/docker-desktop)
- Docker compose
- Java8 (https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot)
- Maven (https://maven.apache.org/download.cgi)
- Anypoint studio (To check the code) (https://www.mulesoft.com/platform/studio)
- Eclipse (To Check the code) (https://www.eclipse.org/downloads/packages/release/kepler/sr1/eclipse-ide-java-developers)

# How to run

## 1. Run Docker

```sh

# Move to the diretory
cd docker-compose

# Below command will run both zookeeper and the kafka broker
docker-compose up -d kafka

# Check logs
docker-compose logs -f 

```
## 2. Create Topics


```sh
# 1. Login to docker container
docker exec -it kafka bash
# 2. Create required topics by using below commands
katopics --zookeeper zookeeper:2181 --create --partitions 2 --replication-factor 1092  --topic rss-feed-input
kafka-topics --zookeeper zookeeper:2181 --create --partitions 2 --replication-factor 1 --topic rss-feed-output
# 3. Verify the topics has created
kafka-topics --zookeeper zookeeper:2181  --list |grep rss-feed


```