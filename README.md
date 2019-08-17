# mustangAPI
A rest API for [Mustangproject](https://www.mustangproject.org)

## Archictecture

Using a
 * Dropwizard and a 
 * Swagger integration
 
 
## Compile

`mvn clean package`

## Start

`java -jar target/zapi-0.0.1-SNAPSHOT.jar server config.yml` 

## Test

use http://127.0.0.1:8080/swagger to try the methods
