package repository

import cats.effect.IO
import doobie.implicits._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import fs2.Stream
import io.getquill.{idiom => _, _}
import model.{Country, CountryNotFoundError}

class CountryRepository(transactor: Transactor[IO]) {

  val dc = new DoobieContext.Postgres(Literal)
  import dc._

  def getCountriesQ: Stream[IO, Country] =
    stream(quote {
      query[Country].sortBy(_.population)(Ord.desc)
    }).transact(transactor)

  def getCountryQ(code: String): IO[Either[CountryNotFoundError.type, Country]] =
    run(quote {
      query[Country].filter(_.code == lift(code))
    }).transact(transactor).map(_.headOption).map {
      case Some(value) => Right(value)
      case _           => Left(CountryNotFoundError)
    }

  def createCountryQ(c: Country): IO[String] =
    run(query[Country].insert(lift(c)).returning(_.code)).transact(transactor)

  def deleteCountryQ(code: String): IO[Either[CountryNotFoundError.type, Unit]] =
    run(query[Country].filter(_.code == lift(code)).delete).transact(transactor).map { affectedRows =>
      if (affectedRows == 1)
        Right(())
      else
        Left(CountryNotFoundError)
    }
}

/*
  def getCountries: Stream[IO, Country] = {
    sql"""select code, name, continent, region, surfacearea, population, localname, governmentform, code2 from country order by population desc"""
      .query[Country].stream.transact(transactor)
  }

  def getCountry(code: String): IO[Either[CountryNotFoundError.type, Country]] = {
    sql"select code, name, continent, region, surfacearea, population, localname, governmentform, code2 from country where code = $code"
      .query[Country].option.transact(transactor).map {
      case Some(value) => Right(value)
      case _ => Left(CountryNotFoundError)
    }
  }

  def createCountry(c: Country): IO[String] = {
    sql"""INSERT INTO country (code, name, continent, region, surfacearea, population, localname, governmentform, code2)
          VALUES (${c.code}, ${c.name}, ${c.continent}, ${c.region}, ${c.surfacearea}, ${c.population}, ${c.localname}, ${c.governmentform}, ${c.code2})"""
      .update.withUniqueGeneratedKeys[String]("code").transact(transactor)
  }

  def deleteCountry(code: String): IO[Either[CountryNotFoundError.type, Unit]] = {
    sql"DELETE FROM country WHERE code = $code".update.run.transact(transactor).map { affectedRows =>
      if (affectedRows == 1) {
        Right(())
      } else {
        Left(CountryNotFoundError)
      }
    }
  }
 */
