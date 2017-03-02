#!/bin/bash

# compile
mvn clean compile assembly:single
# compress
tar -zcvf ./dist/trueno-elastic-bridge-server.tar.gz ./target/trueno-elastic-bridge-server.jar
