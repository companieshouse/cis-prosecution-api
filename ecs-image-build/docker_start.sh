#!/bin/bash
# Start script for cis-api

PORT=8080

exec java -jar -Dserver.port="${PORT}" "cis-prosecution-api.jar"