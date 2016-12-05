package quote

import java.time.ZonedDateTime
import java.util.UUID

import org.scalatest._
import play.api.libs.json.{JsValue, Json}

class QuoteJsonTest extends FlatSpec with Matchers {

  "QuoteJson" should "generate a valid json" in {
    val quote = QuoteJson(UUID.randomUUID().toString, "user ref", "A quote", "An author", ZonedDateTime.now())

    val result: JsValue = Json.toJson(quote)

    (result \ "ref").as[String] should be(quote.ref)
    (result \ "quote").as[String] should be("A quote")
    (result \ "author" \ "name").as[String] should be("An author")
    (result \ "created").as[ZonedDateTime] should be(quote.created)
  }

}
