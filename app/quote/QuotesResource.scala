package quote

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Results}
import quote.ReadQuotesActor.FindAll

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


@Singleton
class QuotesResource @Inject()(quotesAction: QuotesAction)(implicit ex: ExecutionContext) {

  implicit val timeout: Timeout = Timeout(100 nano)
  implicit lazy val system = ActorSystem()

  val readActor = system.actorOf(Props(classOf[ReadQuotesActor]))

  def index: Action[AnyContent] = {
    quotesAction async {
      implicit request => (readActor ? FindAll())
        .mapTo[List[Quote]]
        .map(QuoteJson.mapper)
        .map(quotes => Results.Ok(Json.toJson(quotes)))
    }
  }
}
