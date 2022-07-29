FROM aeron-java8
SHELL [ "/bin/bash", "-o", "pipefail", "-c" ]
WORKDIR /root/jar/
COPY --chmod=755 /build/libs/archive-host-0.1-SNAPSHOT-all.jar /root/jar/archive-host-0.1-SNAPSHOT-all.jar
COPY --chmod=755 entrypoint.sh /root/jar/entrypoint.sh
ENTRYPOINT ["/root/jar/entrypoint.sh"]
