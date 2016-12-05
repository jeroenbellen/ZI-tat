package quote

import javax.inject.Inject

import akka.actor.Actor
import quote.ReadQuotesActor.{FindAll, FindOne}

import scala.concurrent.Future

object ReadQuotesActor {

  case class FindAll(userRef: String)

  case class FindOne(userRef: String, ref: String)

}

class ReadQuotesActor @Inject()(ds: QuotesDataStore) extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  override def receive: Receive = {
    case FindAll(userRef) => Future {
      ds.findAll(userRef)
    } pipeTo sender

    case FindOne(userRef, ref) => Future {
      ds.findOne(userRef, ref)
    } pipeTo sender
  }

}
