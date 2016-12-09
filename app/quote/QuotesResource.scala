package quote

import java.util.UUID
import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result, Results}
import quote.ReadQuotesActor.{FindAll, FindOne}
import quote.WriteQuotesActor.{Put, PutIfExist}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

case class PutQuoteCommand(userRef: String,
                           quote: String,
                           author: String,
                           ref: String)

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
      implicit request => asCommand(userRef, UUID.randomUUID().toString, request) match {
        case Some(command) => put(command)
        case None => Future.successful(Results.BadRequest)
      }
    }

  def update(userRef: String, ref: String): Action[AnyContent] =
    quotesAction async {
      implicit request => asCommand(userRef, ref, request) match {
        case Some(command) => putIfExist(command)
        case None => Future.successful(Results.BadRequest)
      }
    }

  private def put(command: PutQuoteCommand): Future[Result] = {
    (writeActor ? Put(command)).
      mapTo[String].
      map(ref => Results.Created.withHeaders("Location" -> s"http://localhost:9000/users/${command.userRef}/quotes/$ref"))
  }

  private def asCommand(userRef: String, ref: String, request: QuotesRequest[AnyContent]): Option[PutQuoteCommand] = {
    for (
      quote <- request.body.asJson.flatMap(json => (json \ "quote").asOpt[String]);
      author <- request.body.asJson.flatMap(json => (json \ "author").asOpt[String])
    ) yield PutQuoteCommand(userRef, quote, author, ref)
  }

  private def putIfExist(command: PutQuoteCommand): Future[Result] = {
    (writeActor ? PutIfExist(command)).
      mapTo[Option[String]].
      map {
        case Some(_) => Results.NoContent
        case None => Results.NotFound
      }
  }
}