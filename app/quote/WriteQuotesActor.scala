package quote

import javax.inject.Inject

import akka.actor.Actor
import quote.WriteQuotesActor.Create

import scala.concurrent.Future

object WriteQuotesActor {

  case class Create(command: CreateQuoteCommand)

}

class WriteQuotesActor @Inject()(dataStore: QuotesDataStore) extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  override def receive: Receive = {
    case Create(command) => Future {
      dataStore.create(command)
    } pipeTo sender
  }
}
