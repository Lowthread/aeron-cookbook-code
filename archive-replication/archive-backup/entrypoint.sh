#!/bin/sh

java -javaagent:/root/aeron/aeron-agent-1.39.0.jar \
  -Djava.net.preferIPv4Stack=true \
  -Daeron.event.log=admin \
  -Daeron.event.archive.log=all \
  -jar /root/jar/archive-backup-0.1-SNAPSHOT-all.jar
