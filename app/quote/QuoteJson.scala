package quote

import java.time.ZonedDateTime

import play.api.libs.json.{JsValue, Json, Writes}

case class QuoteJson(ref: String,
                     quote: String,
                     author: String,
                     created: ZonedDateTime) {}

object QuoteJson {
  implicit val implicitWrites = new Writes[QuoteJson] {
    override def writes(q: QuoteJson): JsValue = {
      Json.obj(
        "ref" -> q.ref,
        "quote" -> q.quote,
        "author" -> Json.obj(
          "name" -> q.author
        ),
        "created" -> q.created
      )
    }
  }
}