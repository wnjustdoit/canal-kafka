#!/usr/bin/env bash
echo "the canal-kafka application is starting..."
mvn clean install -Plocal
cd canal-kafka-booter/ && mvn spring-boot:run