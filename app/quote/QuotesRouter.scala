package quote

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class QuotesRouter @Inject()(resource: QuotesResource) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/users/$userRef/quotes") => resource.index(userRef)
    case POST(p"/users/$userRef/quotes") => resource.create(userRef)
    case GET(p"/users/$userRef/quotes/$ref") => resource.getOne(userRef, ref)
    case PUT(p"/users/$userRef/quotes/$ref") => resource.update(userRef, ref)
  }
}
