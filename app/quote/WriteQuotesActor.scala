package quote

import javax.inject.Inject

import akka.actor.Actor
import quote.WriteQuotesActor.{Put, PutIfExist}

import scala.concurrent.Future

object WriteQuotesActor {

  case class Put(command: PutQuoteCommand)

  case class PutIfExist(command: PutQuoteCommand)

}

class WriteQuotesActor @Inject()(dataStore: QuotesDataStore) extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  override def receive: Receive = {
    case Put(command) => Future {
      dataStore.put(command)
    } pipeTo sender

    case PutIfExist(command) => Future {
      for (quote <- dataStore.findOne(command.userRef, command.ref);
           ref <- Some(dataStore.put(command))
      ) yield ref
    } pipeTo sender
  }
}
