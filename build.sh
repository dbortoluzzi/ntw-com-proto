#!/bin/bash
cd backend/
./build-backend.sh
cd ../

docker-compose build
