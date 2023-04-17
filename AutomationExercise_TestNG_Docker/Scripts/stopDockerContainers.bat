set ymlDir=%cd%\src\main\DockerComposeFiles
cd %ymlDir%
docker compose -f .\%1 down
docker ps | findstr %2