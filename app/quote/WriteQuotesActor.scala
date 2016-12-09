package quote

import javax.inject.Inject

import akka.actor.Actor
import quote.WriteQuotesActor.{Delete, Put, PutIfExist}

import scala.concurrent.Future

object WriteQuotesActor {

  case class Put(command: PutQuoteCommand)

  case class PutIfExist(command: PutQuoteCommand)

  case class Delete(userRef: String, ref: String)

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

    case Delete(userRef, ref) => Future {
      for (quote <- dataStore.findOne(userRef, ref);
           done <- Some(dataStore.delete(userRef, ref))) yield done
    } pipeTo sender
  }
}
