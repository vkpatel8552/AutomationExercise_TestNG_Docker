set ymlDir=%cd%\src\main\DockerComposeFiles
cd %ymlDir%
docker compose -f .\%1 up --scale %2=1 -d
ping /n 1 /w 60000 localhost:4444 >nul
docker ps | findstr %2