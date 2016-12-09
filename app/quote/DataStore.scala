package quote

import java.util.UUID
import javax.inject.{Inject, Singleton}

import awscala.dynamodbv2.{DynamoDB, DynamoDBCondition}
import com.amazonaws.services.s3.model.Region
import jp.co.bizreach.dynamodb4s.{DynamoAttribute, DynamoHashKey, DynamoRangeKey, DynamoTable}
import play.api.Configuration


object QuotesTable extends DynamoTable {
  val table = "quotes"

  val userRef = DynamoHashKey[String]("user_ref")
  val ref = DynamoRangeKey[String]("ref")
  val quote = DynamoAttribute[String]("quote")
  val author = DynamoAttribute[String]("author")
}

case class QuoteRow(ref: String,
                    user_ref: String,
                    quote: String,
                    author: String)

@Singleton
class QuotesDataStore @Inject()(configuration: Configuration) {

  implicit val region = Region.fromValue(configuration.getString("aws.region").get).toAWSRegion
  implicit val db = DynamoDB(
    configuration.getString("aws.access_key_id").get,
    configuration.getString("aws.secret_access_key").get
  )

  def findAll(userRef: String): List[Quote] =
    QuotesTable.query.
      filter(f => f.userRef -> DynamoDBCondition.eq(userRef) :: Nil).
      limit(1000).
      list[QuoteRow].

      map(row => Quote(row.ref, userRef, row.quote, row.author)).
      toList

  def findOne(userRef: String, ref: String): Option[Quote] =
    QuotesTable.query.
      filter(f => List(f.userRef -> DynamoDBCondition.eq(userRef), f.ref -> DynamoDBCondition.eq(ref))).
      limit(1).
      list[QuoteRow].

      map(row => Quote(row.ref, userRef, row.quote, row.author)).
      find(f => true)

  def put(command: PutQuoteCommand): String = {
    val row: QuoteRow = QuoteRow(
      command.ref,
      command.userRef,
      command.quote,
      command.author
    )
    QuotesTable.put(row)
    row.ref
  }

}