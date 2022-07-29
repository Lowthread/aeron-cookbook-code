FROM azul/zulu-openjdk-debian:8u322

SHELL [ "/bin/bash", "-o", "pipefail", "-c" ]

COPY --chmod=755 setup-docker.sh /root/dockerbuild/setup-docker.sh
RUN /root/dockerbuild/setup-docker.sh && rm --recursive --force "/root/dockerbuild"
