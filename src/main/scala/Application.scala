import cats.effect.{ContextShift, ExitCode, IO, IOApp, Resource, _}
import config.Config
import db.Database
import doobie.Transactor
import doobie.util.ExecutionContexts
import model.Country
import repository.CountryRepository

object Application extends IOApp {

  private val configFile: String = "application.conf"

  override def run(args: List[String]): IO[ExitCode] = {
    (for {
      config     <- Config.load(configFile)
      context    <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      blocker    <- Blocker[IO]
      transactor <- Database.transactor(config.database, context, blocker)
    } yield Resources(transactor, config)).use(execute)
  }

  private def execute(resources: Resources)(implicit concurrentEffect: ConcurrentEffect[IO], timer: Timer[IO]): IO[ExitCode] = {
    val repository = new CountryRepository(resources.transactor)
    for {
      countries <- repository.getCountries.compile.toList
      _         <- IO(println("\nCountries BEFORE INSERT operation"))
      _         <- IO(countries.take(5).foreach(println))

      bigCountry = Country("BIG", "Supper Country", "Europe", "Europe", 1300000000, 1300000000, "Supper Country", "Republic", "BI")
      newCountry <- repository.createCountry(bigCountry).attempt
      _          <- IO(println(s"\nAdded new country with code: $newCountry"))

      _          <- IO(println(s"\nTry again to add new country with code: $newCountry"))
      newCountry2 <- repository.createCountry(bigCountry).attempt
      _ <- newCountry2 match {
        case Left(e)      => IO(println(s"Error occurred in creation new country: $e"))
        case Right(value) => IO(println(s"Added new country with code: $value"))
      }

      countries2 <- repository.getCountries.compile.toList
      _          <- IO(println("\nCountries AFTER INSERT operation"))
      _          <- IO(countries2.take(5).foreach(println))

      _          <- repository.deleteCountry("BIG")
      countries3 <- repository.getCountries.compile.toList
      _          <- IO(println("\nCountries AFTER DELETE operation"))
      _          <- IO(countries3.take(5).foreach(println))

    } yield ExitCode.Success
  }

  case class Resources(transactor: Transactor[IO], config: Config)
}
