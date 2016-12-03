package quote

import javax.inject.Inject

import play.api.http.HttpVerbs
import play.api.mvc.{ActionBuilder, Request, Result}

import scala.concurrent.{ExecutionContext, Future}


class QuotesAction @Inject()(implicit ex: ExecutionContext)
  extends ActionBuilder[QuotesRequest]
    with HttpVerbs {

  override def invokeBlock[A](request: Request[A], block: (QuotesRequest[A]) => Future[Result]): Future[Result] =
    block {
      QuotesRequest(request)
    }
}
