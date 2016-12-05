package quote

import java.time.ZonedDateTime
import java.util.UUID

case class Quote(ref: String = UUID.randomUUID().toString,
                 userRef: String,
                 quote: String,
                 author: String,
                 created: ZonedDateTime = ZonedDateTime.now()) {

}
