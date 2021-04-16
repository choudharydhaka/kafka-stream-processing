# kafka-stream-processing
Kafka Stream Processing



katopics --zookeeper localhost:2181 --create --partitions 2 --replication-factor 1092  --topic rss-feed-input
kafka-topics --zookeeper localhost:2181 --create --partitions 2 --replication-factor 1 --topic rss-feed-output