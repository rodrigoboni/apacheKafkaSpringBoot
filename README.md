
# Apache Kafka for Developers using Spring Boot

* [Course link](https://www.udemy.com/share/102AWn3@n0xeHuuw9iPcv5iCP_b5hMtJ0rOgWV52hXvUWE67xD3VKldTSW4mhGhMEF8Zkeg=/)
* [Course code](https://github.com/dilipsundarraj1/kafka-for-developers-using-spring-boot)

## Running Kafka locally

* Kafka may be configured through [binaries](https://kafka.apache.org/quickstart) or [docker images](https://github.com/confluentinc/cp-docker-images)
* In this course binaries options is used (follow instructions in above quick start link)
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
* Kafka commands / binaries
  * In the bin folder there are some scripts to provide access to Kafka resources
  * Execute the scripts without parameters to show the script help / avaiable parameters

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

* Message Order
  * Kafka guarantee order of message delivery only at partition level
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
