import cats.effect.{ContextShift, ExitCode, IO, IOApp, Resource, _}
import config.Config
import db.Database
import doobie.Transactor
import doobie.util.ExecutionContexts
import model.Country
import repository.CountryRepository

object Application extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = resources("application.conf").use(create)

  case class Resources(transactor: Transactor[IO], config: Config)

  private def resources(configFile: String)(implicit contextShift: ContextShift[IO]): Resource[IO, Resources] =
    for {
      config     <- Config.load(configFile)
      ec         <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      blocker    <- Blocker[IO]
      transactor <- Database.transactor(config.database, ec, blocker)
    } yield Resources(transactor, config)

  private def create(resources: Resources)(implicit concurrentEffect: ConcurrentEffect[IO], timer: Timer[IO]): IO[ExitCode] = {
    val repository = new CountryRepository(resources.transactor)
    for {
      countries <- repository.getCountriesQ.compile.toList
      _         <- IO(println("Countries BEFORE INSERT operation"))
      _         <- IO(countries.take(5).foreach(println))

      bigCountry = Country("BIG", "Big Country", "Europe", "Europe", 1277558000, 1277558000, "Supper Country", "Republic", "BI")
      newCountry <- repository.createCountryQ(bigCountry).attempt
      _          <- IO(println(s"Added new country with code: $newCountry"))

      newCountry2 <- repository.createCountryQ(bigCountry).attempt
      _ <- newCountry2 match {
        case Left(e)      => IO(println(s"Error occurred in creation new country: $e"))
        case Right(value) => IO(println(s"Added new country with code: $value"))
      }

      countries2 <- repository.getCountriesQ.compile.toList
      _          <- IO(println("Countries AFTER INSERT operation"))
      _          <- IO(countries2.take(5).foreach(println))

      _          <- repository.deleteCountryQ("BIG")
      countries3 <- repository.getCountriesQ.compile.toList
      _          <- IO(println("Countries AFTER DELETE operation"))
      _          <- IO(countries3.take(5).foreach(println))

    } yield ExitCode.Success
  }
}
