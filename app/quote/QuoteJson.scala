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

  def apply(quote: Quote): QuoteJson = new QuoteJson(quote.ref, quote.quote, quote.author, quote.created)

  def mapper: (List[Quote] => List[QuoteJson]) = (quotes: List[Quote]) => quotes.map(QuoteJson(_))
}