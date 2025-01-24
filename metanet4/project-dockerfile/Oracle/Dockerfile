FROM gvenzl/oracle-xe

ENV ORACLE_PASSWORD=1234

RUN [ ! -d "/opt/oracle/oradata" ] || rm -rf /opt/oracle/oradata
RUN mkdir -p /opt/oracle/oradata

VOLUME [ "/oradata:/opt/oracle/oradata" ]

COPY 001_projectDB.sql /container-entrypoint-initdb.d/

EXPOSE 1521
