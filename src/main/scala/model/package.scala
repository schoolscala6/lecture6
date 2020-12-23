package object model {

  case class Country(
      code: String,
      name: String,
      continent: String,
      region: String,
      surfacearea: Int,
      population: Int,
      localname: String,
      governmentform: String,
      code2: String
  )

  case object CountryNotFoundError

}
