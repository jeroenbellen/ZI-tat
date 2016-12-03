package quote

import akka.actor.Actor
import quote.ReadQuotesActor.FindAll

import scala.concurrent.Future

object ReadQuotesActor {

  val quotes = List(
    Quote(quote = "At any street corner the feeling of absurdity can strike any man in the face.", author = "Albert Camus"),
    Quote(quote = "They always say time changes things, but you actually have to change them yourself.", author = "Andy Warhol")
  )

  case class FindAll()

}

class ReadQuotesActor extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  override def receive: Receive = {
    case FindAll() => Future {
      ReadQuotesActor.quotes
    } pipeTo sender
  }

}
