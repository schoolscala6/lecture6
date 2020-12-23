import cats.effect.IO
import doobie.implicits._
import doobie.scalatest._
import doobie.util.transactor.Transactor
import model.Country
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repository.CountryRepository

import scala.concurrent.ExecutionContext

class CountryRepositorySpec extends AnyWordSpec with Matchers with IOChecker {
  def transactor: Transactor[IO] = {
    implicit val cs: cats.effect.ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val createSql = sql"""create table country
                         |(
                         |    code           varchar not null,
                         |    name           varchar not null,
                         |    continent      varchar not null,
                         |    region         varchar not null,
                         |    surfacearea    real    not null,
                         |    indepyear      smallint,
                         |    population     integer not null,
                         |    lifeexpectancy real,
                         |    gnp            numeric(10, 2),
                         |    gnpold         numeric(10, 2),
                         |    localname      varchar not null,
                         |    governmentform varchar not null,
                         |    headofstate    varchar,
                         |    capital        integer,
                         |    code2          varchar not null
                         |)""".stripMargin

    val tx = Transactor.fromDriverManager[IO]("org.h2.Driver", "jdbc:h2:mem:test_sb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "")

    createSql.update.run
      .transact(tx)
      .unsafeRunSync()

    tx
  }

  private lazy val repository = new CountryRepository(transactor)

  "Country Repository" should {
    "insert a country" in {
      val bigCountry = Country("BIG", "Big Country", "Europe", "Europe", 1277558000, 1277558000, "Supper Country", "Republic", "BI")
      val result     = repository.createCountryQ(bigCountry).unsafeRunSync()
      result shouldBe "BIG"
    }

    "get all countries" in {
      val countries = repository.getCountriesQ.compile.toList.unsafeRunSync()
      countries.size shouldBe 1
      countries.head.code shouldBe "BIG"
    }

    "delete country" in {
      repository.deleteCountryQ("BIG")
      val countries = repository.getCountriesQ.compile.toList.unsafeRunSync()
      countries.size shouldBe 1
      countries.head.code shouldBe "BIG"
    }
  }
}
