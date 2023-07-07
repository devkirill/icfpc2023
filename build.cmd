@echo off

call gradlew clean

call gradlew build --no-daemon

del "build\libs\*-plain.jar"

docker build -t %DOCKER_USER%/icfpc2023:latest .
docker push %DOCKER_USER%/icfpc2023:latest
