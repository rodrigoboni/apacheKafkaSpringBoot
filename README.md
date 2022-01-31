
# Apache Kafka for Developers using Spring Boot

* [Course link](https://www.udemy.com/share/102AWn3@n0xeHuuw9iPcv5iCP_b5hMtJ0rOgWV52hXvUWE67xD3VKldTSW4mhGhMEF8Zkeg=/)
* [Course code and notes](https://github.com/dilipsundarraj1/kafka-for-developers-using-spring-boot)
* Course PDF in root folder

## Running Kafka locally

* https://lankydan.dev/running-kafka-locally-with-docker
* https://hub.docker.com/r/bitnami/kafka/
* https://github.com/bitnami/bitnami-docker-kafka

```
# start cluster
cd kafka
docker-compose up -d

# stop cluster
docker-compose stop

# create a topic
docker exec -it kafka_kafka_1 kafka-topics.sh --create --bootstrap-server kafka:9092 --topic my-topic --partitions 2 --replication-factor 1

# list avaiable topics
docker exec -it kafka_kafka_1 kafka-topics.sh --bootstrap-server kafka:9092 --list

# Produce and consume messages (run in separated terminal session)
docker exec -it kafka_kafka_1 kafka-console-producer.sh --bootstrap-server kafka:9092 --topic my-topic
docker exec -it kafka_kafka_1 kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic my-topic --from-beginning
```
