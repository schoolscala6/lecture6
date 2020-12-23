# Scala School: Seminar 6 - Doobie

###Doobie Documentation

https://tpolecat.github.io/doobie/docs/01-Introduction.html

###Setup postgres

```
curl -O https://raw.githubusercontent.com/tpolecat/doobie/series/0.7.x/world.sql
psql -c 'create user postgres createdb' postgres
psql -c 'create database world;' -U postgres
psql -c '\i world.sql' -d world -U postgres
psql -d world -c "create type myenum as enum ('foo', 'bar')" -U postgres
psql -d world -c "create extension postgis" -U postgres
```

###Run application

```sbt run```

###Test application

```sbt test```