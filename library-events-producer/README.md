# Apache Kafka for Developers using Spring Boot - Producer project

## Setup
* Java JDK 11 is required
* Enable annotation processors in Intellij IDEA for lombok annotations processing

## REST API

* post library event
```
curl --location --request POST 'http://localhost:8080/v1/libraryevent' \
--header 'Content-Type: application/json' \
--data-raw '{
    "libraryEventId": null,
    "book": {
        "bookId": 123,
        "bookName": "era uma vez",
        "bookAuthor": "zé ruela"
    }
}'
```

* put library event
```
curl --location --request PUT 'http://localhost:8080/v1/libraryevent' \
--header 'Content-Type: application/json' \
--data-raw '{
    "libraryEventId": 420555486,
    "book": {
        "bookId": 123,
        "bookName": "era uma vez",
        "bookAuthor": "zé ruela"
    }
}'
```
