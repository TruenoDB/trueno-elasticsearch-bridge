#!/bin/bash

# compile
mvn clean compile assembly:single

# copy to a temporary directory
rm -rf ./build/*
mkdir ./build/server
cp ./target/trueno-elastic-bridge-server.jar ./build/server/
cp ./bridgeServer.sh ./build/server/
cd ./build

# compress
tar -zcvf trueno-elastic-bridge-server.tar.gz ./server
cd ..
rm -rf ./build/server