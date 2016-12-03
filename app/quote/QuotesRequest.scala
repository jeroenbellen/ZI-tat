package quote

import play.api.mvc.{Request, WrappedRequest}

object QuotesRequest {
  def apply[A](request: Request[A]): QuotesRequest[A] = new QuotesRequest(request)
}

class QuotesRequest[A](request: Request[A])
  extends WrappedRequest(request)