build the image:

./build.sh

run the container:

docker run --rm -p 2480:2480 \
    -p 2424:2424 \
    --env ORIENTDB_ROOT_PASSWORD="arcade" \
    arcade/orientdb-demokit


compose yml snippet

version: '2'
services:
  arcadeanalytics-orientdb-demokit:
    image: arcade/orientdb-demokit:latest
    environment:
      ORIENTDB_ROOT_PASSWORD: "arcade"
