build the image:

./build.sh

run the container:

docker run --rm \
    -p 2480:2480 \
    -p 2424:2424 \
    --env ORIENTDB_ROOT_PASSWORD="rootpasswd" \
    robfrank/orientdb


compose yml snippet

version: '2'
services:
  orientdb-demokit:
    image: robfrank/orientdb:latest
    environment:
      ORIENTDB_ROOT_PASSWORD: "rootpasswd"
