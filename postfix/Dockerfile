FROM debian:13.3-slim

RUN apt update && DEBIAN_FRONTEND=noninteractive apt install -y \
    postfix \
    python3-psycopg \
    locales \
    && rm -rf /var/lib/apt/lists/*

COPY main.cf /etc/postfix/main.cf
COPY deliver.py /usr/local/bin/deliver.py
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]