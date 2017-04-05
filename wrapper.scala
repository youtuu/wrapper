package lib

import twitter4j._
import twitter4j.conf._
import twitter4j.Query
import twitter4j.QueryResult
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory

import java.lang._
import java.util.Date
import scala.collection.JavaConversions._

import org.joda.time._

case class TwitterStatus(userId:          Long
                        ,userName:        String
                        ,userScreen:      String
                        ,tweetId:         Long
                        ,tweetDatetime:   DateTime
                        ,tweetText:       String
                        )

class Twitter4jWrapper(key: String, secret: String, token: String, tokenSecret: String) {
    private val cb = new ConfigurationBuilder

    cb.setOAuthConsumerKey(key)
      .setOAuthConsumerSecret(secret)
      .setOAuthAccessToken(token)
      .setOAuthAccessTokenSecret(tokenSecret)

    private val twitterFactory = new TwitterFactory(cb.build)
    private val twitterInstance = twitterFactory.getInstance

    def search(search: String, count: Int): List[TwitterStatus] = {
      val query = new Query(search)
      query.setCount(count)
      getTwitterStatus( twitterInstance.search(query) )
    }

    private
    def getTwitterStatus(query: QueryResult): List[TwitterStatus] = {
      query.getTweets
           .map {(s: Status) => TwitterStatus(s.getUser.getId
                                             ,s.getUser.getName
                                             ,s.getUser.getScreenName
                                             ,s.getId
                                             ,toDateTime(s.getCreatedAt)
                                             ,s.getText
                                             )
                }.toList
    }

    private def toDateTime(queryDate: Date): DateTime = {
      val ds = queryDate.toString.split(' ')
      val (year, month, date, time) =  (ds(5), toMonth(ds(1)), ds(2), ds(3))
      new DateTime(s"${ year }-${ month }-${ date }T${ time }")
    }

    private def toMonth(m: String): Int = {
      Map("Jan" -> 1, "Feb" ->  2, "Mar" ->  3, "Apr" ->  4,
          "May" -> 5, "Jun" ->  6, "jul" ->  7, "Aug" ->  8,
          "Sep" -> 9, "Oct" -> 10, "Nov" -> 11, "Dec" -> 12
          ).get(m).getOrElse(0)
    }
}
