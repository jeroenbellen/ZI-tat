package quote

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Results}
import quote.ReadQuotesActor.{FindAll, FindOne}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


@Singleton
class QuotesResource @Inject()(quotesAction: QuotesAction, @Named("readQuotesActor") readActor: ActorRef)(implicit ex: ExecutionContext) {

  implicit val timeout: Timeout = 1.seconds

  def index(userRef: String): Action[AnyContent] =
    quotesAction async {
      implicit request => (readActor ? FindAll(userRef))
        .mapTo[List[Quote]]
        .map(QuoteJson.mapper)
        .map(quotes => Results.Ok(Json.toJson(quotes)))
    }

  def getOne(userRef: String, ref: String): Action[AnyContent] =
    quotesAction async {
      implicit request => (readActor ? FindOne(userRef, ref))
        .mapTo[Option[Quote]]
        .map {
          case Some(quote) => Results.Ok(Json.toJson(QuoteJson(quote)))
          case None => Results.NotFound
        }
    }
}
