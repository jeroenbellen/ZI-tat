package quote

import javax.inject.Inject

import akka.actor.Actor
import quote.ReadQuotesActor.FindAll

import scala.concurrent.Future

object ReadQuotesActor {

  case class FindAll()

}

class ReadQuotesActor @Inject()(ds: QuotesDataStore) extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  override def receive: Receive = {
    case FindAll() => Future {
      ds.findAll()
    } pipeTo sender
  }

}
