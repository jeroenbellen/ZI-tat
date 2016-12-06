package quote

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Results}
import quote.ReadQuotesActor.{FindAll, FindOne}
import quote.WriteQuotesActor.Create

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

case class CreateQuoteCommand(userRef: String,
                              quote: String,
                              author: String)

@Singleton
class QuotesResource @Inject()(quotesAction: QuotesAction,
                               @Named("readQuotesActor") readActor: ActorRef,
                               @Named("writeQuotesActor") writeActor: ActorRef)(implicit ex: ExecutionContext) {

  implicit val timeout: Timeout = 1.seconds

  def index(userRef: String): Action[AnyContent] =
    quotesAction async {
      implicit request => (readActor ? FindAll(userRef)).
        mapTo[List[Quote]].
        map(QuoteJson.mapper).
        map(quotes => Results.Ok(Json.toJson(quotes)))
    }

  def getOne(userRef: String, ref: String): Action[AnyContent] =
    quotesAction async {
      implicit request => (readActor ? FindOne(userRef, ref)).
        mapTo[Option[Quote]].
        map {
          case Some(quote) => Results.Ok(Json.toJson(QuoteJson(quote)))
          case None => Results.NotFound
        }
    }

  def create(userRef: String): Action[AnyContent] =
    quotesAction async {
      implicit request => {
        {
          for (
            quote <- request.body.asJson.flatMap(json => (json \ "quote").asOpt[String]);
            author <- request.body.asJson.flatMap(json => (json \ "author").asOpt[String])
          ) yield CreateQuoteCommand(userRef, quote, author)

        } match {
          case Some(command) =>
            (writeActor ? Create(command)).
              mapTo[String].
              map(ref => Results.Created.withHeaders("Location" -> s"http://localhost:9000/users/${command.userRef}/quotes/$ref"))

          case None => Future.successful(Results.BadRequest)
        }
      }
    }
}
