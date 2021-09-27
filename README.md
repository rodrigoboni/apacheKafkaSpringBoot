
# Apache Kafka for Developers using Spring Boot

* [Course link](https://www.udemy.com/share/102AWn3@n0xeHuuw9iPcv5iCP_b5hMtJ0rOgWV52hXvUWE67xD3VKldTSW4mhGhMEF8Zkeg=/)
* [Course code and notes](https://github.com/dilipsundarraj1/kafka-for-developers-using-spring-boot)
* Course PDF in root folder

## Running Kafka locally

* Kafka may be configured through [binaries](https://kafka.apache.org/quickstart) or [docker images](https://github.com/wurstmeister/kafka-docker)
* Using binaries for local kafka spin up:
  * Use a folder without spaces in name (to avoid kafka start up errors)
  * Download and extract binaries
  * Edit /config/server.properties file:
      ```
      listeners=PLAINTEXT://localhost:9092
      ## this parameters blocks Kafka of create a topic that doesn't exists when a message is produced
      auto.create.topics.enable=false
      ```

  * Start zookeeper:
      ```
      bin/zookeeper-server-start.sh config/zookeeper.properties
      ```

  * Start broker:
      ```
      bin/kafka-server-start.sh config/server.properties
      ```

* Using docker compose
  * clone git repository <https://github.com/wurstmeister/kafka-docker>
  * edit docker-compose.yml file and:
    * set KAFKA_ADVERTISED_HOST_NAME with your current IP
    * set auto create topics with var env in docker-compose.yml: KAFKA_CREATE_TOPICS: "topic1:1:1" (topic1 with 1 partition and 1 replica for instance)
  * start / stop kafka:
    ```
    docker-compose up -d
    docker-compose stop
    ```

## Using Kafka

* Create topic
  ```
  bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092 --partitions 4 --replication-factor 1
  ```

* Instantiate a producer (without key)
  ```

  bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic
  ```

* Instantiate a consumer (without key)
  ```
  bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning
  ```

## Message Order

* Kafka guarantee order of messages only at partition level
* By default Kafka broker distributes produced messages through all partitions, and consumer polls messages from all partitions (to improve performance)
* If message delivery order is required, then these messages must be send to the same partition
* This could be achieved by setting a key / value to messages
* All messages with the same key / value are persisted in the same partition (when the first message arrives the kafka broker algorithm determines wich partition will be used)
* To send a message with key:
  ```
  bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic --property "key.separator=-" --property "parse.key=true"
  # message example: "A-Apple" indicates "A" value for key and "Apple" as message
  ```

* To receive a message with key (garantee of order):
  ```
  bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning -property "key.separator= - " --property "print.key=true"
  ```

## Offsets

* Offsets are like bookmarks - it sets where a consumer stop to reading messages in a partition
* When polling messages from a topic / partition, a consumer have some options:
  * from-beginning - Read from the first message in some partition
  * latest - Read after the latest message read before last consumer spin up
  * specific offset
* Kafka broker manages consumer / group offsets with the _consumer_offsets (auto created and updated by kafka broker)

## Consumer groups

* Consumer groups are the base for scalable message consumption
* For example, in a topic with 4 partitions there can be up to 4 consumers to consume messages in a paralel way
* Initially the application may instantiate 2 consumers with same group id, which result in each of 2 consumers consuming messages from 2 partitions each
* If messages are produced faster then consumed by the 2 consumers in same group, the topic may have consumer lag
* This way a solution could be instantiate 2 more consumers, with same group id
* With 2 new consumers, kafka broker rearranges the consumers, resulting in a consumer for each partition, providing more parallel message processing
* If there are more consumers then partitions, some consumers will stay idle
* Consumer group it's assigned automatically by kafka broker if not specified by a consumer (in group.id property)

## Commit log

* When a message is produced and sent to a topic, the broker assign a partition and then the message is recorded in the commit-log of the choosen partition
* Commit-log is writen in the file system of kafka broker, and is writen in bytes
* The logs folder is defined in the log.dirs property in server.properties file  (default /tmp/kafka-logs)
* Each partition has it own log file (with .log extension)

## Retention policy

* Retention policy determines how long a message should be retained in kafka broker / file system
* This period is defined in log.retention.hours property of server.properties files
* The default retention period is 7 days (168 hours)
