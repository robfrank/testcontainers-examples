############################################################
# Dockerfile to run an OrientDB (Graph) Container
############################################################

FROM orientdb:3.1.2-tp3

#overwrite gremlin server config
ADD resources/orientdb-server-config.xml /orientdb/config/
ADD resources/gremlin-server.yaml /orientdb/config/
ADD resources/openbeer.groovy /orientdb/config/
ADD resources/openbeer.properties /orientdb/config/

ADD openbeer ./databases/openbeer/

#Gremlin http
EXPOSE 8182



