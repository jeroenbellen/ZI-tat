package quote

import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Results}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class QuotesResource @Inject()(quotesAction: QuotesAction)(implicit ex: ExecutionContext) {

  val quotes = List(
    QuoteJson(UUID.randomUUID().toString,
      "At any street corner the feeling of absurdity can strike any man in the face.",
      "Albert Camus",
      ZonedDateTime.now()
    ),
    QuoteJson(UUID.randomUUID().toString,
      "They always say time changes things, but you actually have to change them yourself.",
      "Andy Warhol",
      ZonedDateTime.now()
    )
  )

  def index: Action[AnyContent] = {
    quotesAction async {
      implicit request => Future.successful {
        Results.Ok(Json.toJson(quotes))
      }
    }
  }
}
