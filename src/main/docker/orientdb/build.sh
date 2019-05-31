#!/usr/bin/env bash

wget -O ./openbeer.zip  https://orientdb.com/public-databases/OpenBeer.zip  \
 && mkdir -p ./openbeer \
 && unzip -d ./openbeer ./openbeer.zip \
 && rm ./openbeer.zip


docker build . -t  robfrank/orientdb --pull --no-cache

rm -rf ./openbeer
