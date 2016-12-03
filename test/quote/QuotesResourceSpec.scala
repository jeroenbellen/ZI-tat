package quote

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.JsValue

class QuotesResourceSpec
  extends PlaySpec
    with OneServerPerSuite
    with ScalaFutures
    with IntegrationPatience {

  def index = {
    wsUrl("/users/me/quotes").get.futureValue
  }

  "GET /users/me/quotes" must {

    "respond with 200 OK" in {
      val response = index
      response.status must be(200)
      response.statusText must be("OK")
    }

    "response in json" in {
      index.header("Content-type").get must be("application/json")
    }

    "contain valid json quotes" in {
      val json: JsValue = index.json
      (json \ 0 \ "quote").as[String] must be("At any street corner the feeling of absurdity can strike any man in the face.")
      (json \ 1 \ "quote").as[String] must be("They always say time changes things, but you actually have to change them yourself.")
    }
  }

}
