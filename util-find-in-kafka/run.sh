#!/bin/sh

mvn clean package > 1_compile.log

if [ $? -eq 0 ]
then
  echo "Success: Compile."
else
  echo "Failure: Compile" >&2
  exit 1
fi

#java -jar -DBOOTSTRAP_SERVERS=localhost:29092,localhost:39092,localhost:49092 ./target/kafka-tracer-1.0.8-SNAPSHOT.jar
