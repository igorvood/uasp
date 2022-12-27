#!/bin/sh

## 1. Run the date command ##
mvn clean package > 1_compile.log

if [ $? -eq 0 ]
then
  echo "Success: Compile."
else
  echo "Failure: Compile" >&2
  exit 1
fi

docker-compose up