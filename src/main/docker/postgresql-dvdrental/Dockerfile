FROM postgres:10


COPY ./dump/dvdrental.tar /tmp/dvdrental.tar

COPY 1-init.sql /docker-entrypoint-initdb.d/
COPY 2-restore-dump.sh /docker-entrypoint-initdb.d/
