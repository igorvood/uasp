#!/bin/sh

#curl -X POST -H "Expect:" -F "jarfile=/target/uasp-streaming-mdm-enrichment-1.7.11.jar" http://localhost:8081/jars/upload

curl -X POST -H "Expect:" -F "jarfile=/home/vood/IdeaProjects/uasp/uasp-streaming-mdm-enrichment/target/uasp-streaming-mdm-enrichment-1.7.11.jar" http://localhost:8081/jars/upload

