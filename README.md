# Scala School: Seminar 6 - Doobie

### Doobie Documentation

https://tpolecat.github.io/doobie/docs/01-Introduction.html

### Setup postgres

https://hub.docker.com/_/postgres

```
curl -O https://raw.githubusercontent.com/tpolecat/doobie/series/0.7.x/world.sql

docker run --name postgres \
-p 5432:5432 \
-e POSTGRES_PASSWORD=postgres \
-e POSTGRES_DB=world \
-v $(pwd)/world.sql:/world.sql -d postgres

docker exec -it postgres psql -U postgres -d world -a -f world.sql
```

### Run application

```sbt run```

### Test application

```sbt test```

### Links
- https://getquill.io/#quotation-introduction
- https://blog.softwaremill.com/testing-doobie-programs-425517c1c295
- https://github.com/jaspervz/todo-http4s-doobie
