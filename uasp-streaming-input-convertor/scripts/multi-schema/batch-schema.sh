#!/usr/bin/env bash

SCHEMA_DIR=./schemas

#Exit immediately if a command exits with a non-zero status.
set -e

if [ $# -ne 2 ]
  then
    echo -e "example: ./batch-schema.sh ift ./schemas"
    exit 1
fi

SCHEMA_DIR=$2

for schema_file in ${SCHEMA_DIR}/*.json; do
  echo "schema_file: $schema_file"
  ./single-schema.sh $1 ${schema_file}
done