package quote

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
                    quote: String,
                    author: String)

@Singleton
class QuotesDataStore @Inject()(configuration: Configuration) {

  implicit val region = Region.fromValue(configuration.getString("aws.region").get).toAWSRegion
  implicit val db = DynamoDB(
    configuration.getString("aws.access_key_id").get,
    configuration.getString("aws.secret_access_key").get
  )

  def findAll(): List[Quote] = {
    QuotesTable.query.
      filter(f => f.userRef -> DynamoDBCondition.eq("me") :: Nil).
      limit(1000).
      list[QuoteRow].

      map(row => Quote(row.ref, row.quote, row.author)).
      toList
  }
}