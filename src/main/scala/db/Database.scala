package db

import cats.effect.{Blocker, ContextShift, IO, Resource}
import config.DatabaseConfig
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object Database {
  // Транзактор, получает соединения от java.sql.DriverManager и выполняет блокирующие операции используя синхронный ExecutionContext
  def transactor(config: DatabaseConfig, ex: ExecutionContext, blocker: Blocker)(implicit
      cs: ContextShift[IO]
  ): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      config.driver,
      config.url,
      config.user,
      config.password,
      ex,
      blocker
    )
}
