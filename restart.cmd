docker compose -p icfpc down
docker compose -p icfpc pull
docker compose -p icfpc up -d

start http://localhost:9999/?token=easy