#!/bin/sh

java -javaagent:/root/aeron/aeron-agent-1.34.0.jar \
  -Djava.net.preferIPv4Stack=true \
  -Daeron.event.log=admin \
  -jar /root/jar/aeron-mdc-publisher-0.1-SNAPSHOT-all.jar
