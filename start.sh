#!/usr/bin/env bash

docker network inspect ptb-backend &> /dev/null || docker network create ptb-backend
docker network inspect module-service_db &> /dev/null || docker network create module-service_db

docker-compose -p ptb-archilab-io up -d 
