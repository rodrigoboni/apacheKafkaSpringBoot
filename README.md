
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
## Projects developed in course

* Producer - library-events-producer
* Consumer - ??
