#!/usr/bin/env bash

#set -e
source .env

TopicsArray=("dev_ivr__uasp_realtime__mdm_enrichment__08__cross_link__avl" "dev_ivr__uasp_realtime__mdm_enrichment__08__cross_link_status__avl" "dev_ivr__uasp_realtime__mdm_enrichment__08__way4_issuing_operation__avl" "dev_ivr__uasp_realtime__mdm_enrichment__08__dlq__avl" "dev_ivr__uasp_realtime__mdm_enrichment__08__avl")
#TopicsArray=("dev_ivr__uasp_realtime__way4_issuing_operation__json_one" "dev_ivr__uasp_realtime__way4_issuing_operation__uaspdto_one" "dev_ivr__uasp_realtime__way4_issuing_operation__dlq_one" )

for topic in ${TopicsArray[*]}; do
   echo "Delete $topic ..."
   kafka-topics.sh --delete --topic $topic --bootstrap-server $BOOTSTRAP_SERVERS --command-config ./config/command.properties
   echo "Sleep ..."
   sleep 2
   echo "Create $topic ..."
   kafka-topics.sh --create --topic $topic --replication-factor 3 --partitions 4 --config retention.ms=$RETENTION_MS --bootstrap-server $BOOTSTRAP_SERVERS --command-config ./config/command.properties
done
