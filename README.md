## Assumptions:
* Purchase and Details are two isolated entities and the id is the same on both (Details are composite of Purchase)
    * This should be guaranteed bye the persistence layer
* Every Purchase have details
* Isn't TimeZones aware
* Didn't implement any king on cache on controller (list)
    * If put cache on Purchase service or controller need a broker and logic to guarantee the consistency of
                the data, maybe a message bus (to some eventually consistency)
* On insert or update the request should send all the values to the fields (no allow patch to the record)
* The Purchase and Details model is a internal representation but to external API combine both (PurchaseExposed model)
    * You can't insert/update only purchase or details
 * Id should be generated inside the persistance layer



## Scale out:
* To scale out the microservice you should create multiple instances as you need and put a load balancer on front or distribute the clients between instances
    * The persistence layer should guarantee the consistency of data

## Notes:
* Metrics is lazy, you only get metrics after the first request to each one
* List Purchases isn't paged
* Api is exposed without any kind of authentication (unsecure)
* Api is stateless



## TODO:
* Test metrics
* Better unit tests (exceptions tests, ...)


## AvailableMetrics:
### DAO:
* allPruchases - timer
    * counter
    * time
    * percentile (75, 95, 98, 99, 999)
    * one/five/fifteen minutes rate request
    * mean rate
    * max
    * min
    * standard deviation
* insert -> timer (see above)
* update -> timer (see above)

### Controller:
* Divide the metrics in 3 classes
    * Errors
    * Success
    * Errors and Success (to easy access the summary rate)
        * for both methods get and post on /api/v1/purchases/ have the three timers
    * com.example.microservice.metric.MetricsService.com.example.microservice.controller.PurchasesController.index.timer.(success|error|requests)
    * com.example.microservice.metric.MetricsService.com.example.microservice.controller.PurchasesController.insertOrChange.timer.(success|error|requests)
## Readme:
### Testing and Compile
    `mvn package`
(will automatically run unit test)
### Generate Java docs
    `mvn javadocs:javadoc`
open ./target/site/apidocs/index.html

### Start server
    java -jar  target/microservice-1.0-SNAPSHOT.jar

with jmxremote enable

    java -jar  target/microservice-1.0-SNAPSHOT.jar \
            -Dcom.sun.management.jmxremote \
            -Dcom.sun.management.jmxremote.port=9010 \
            -Dcom.sun.management.jmxremote.local.only=false \
            -Dcom.sun.management.jmxremote.authenticate=false \
            -Dcom.sun.management.jmxremote.ssl=false


### List purchases
    curl http://localhost:8080/api/v1/purchases/

### Create purchases:
#### Not expired:
    curl -H "Content-Type: application/json" -X POST -d '{"productType": "bid", "expires": "2017-01-01T00:00:01", "description": "sports bid", "quantity": 1, "value": 2.2}' http://localhost:8080/api/v1/purchases/
#### Expired:
    curl -H "Content-Type: application/json" -X POST -d '{"productType": "bid", "expires": "2015-01-01T00:00:01", "description": "sports bid", "quantity": 1, "value": 2.2}' http://localhost:8080/api/v1/purchases/

### Update purchase
    curl -H "Content-Type: application/json" -X POST -d '{"id": 1,"productType": "bid", "expires": "2018-01-01T00:00:01", "description": "sports bid changed", "quantity": 10, "value": 20.54}' http://localhost:8080/api/v1/purchases/


### Get metrics
    curl http://localhost:8080/metrics

also is enable in jmx in:
org.springframework.boot -> Endpoint -> metricsEndpoint

or (isn't the same "view")

metrics -> (this where the dropwizard put the metrics)