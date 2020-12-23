import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.module.catseffect.syntax._
import pureconfig.generic.auto._

package object config {

  case class DatabaseConfig(driver: String, url: String, user: String, password: String, threadPoolSize: Int)

  case class Config(database: DatabaseConfig)

  object Config {
    def load(configFile: String)(implicit cs: ContextShift[IO]): Resource[IO, Config] =
      Blocker[IO].flatMap { blocker =>
        Resource.liftF(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[IO, Config](blocker))
      }
  }

}
