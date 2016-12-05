package quote

import java.time.ZonedDateTime

import play.api.libs.json.{JsValue, Json, Writes}

case class QuoteJson(ref: String,
                     userRef: String,
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
        "created" -> q.created,

        "links" -> Json.arr(
          Json.obj(
            "rel" -> "self",
            "href" -> s"http://localhost:9000/users/${q.userRef}/quotes/${q.ref}"
          )
        )
      )
    }
  }

  def apply(quote: Quote): QuoteJson = new QuoteJson(quote.ref, quote.userRef, quote.quote, quote.author, quote.created)

  def mapper: (List[Quote] => List[QuoteJson]) = (quotes: List[Quote]) => quotes.map(QuoteJson(_))
}