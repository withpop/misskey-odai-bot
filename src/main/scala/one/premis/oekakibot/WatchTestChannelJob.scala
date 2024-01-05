package one.premis.oekakibot

import com.typesafe.scalalogging.StrictLogging
import misskey4j.MisskeyFactory
import misskey4j.api.request.ChannelsTimelineRequest
import org.quartz.{Job, JobExecutionContext}
import scala.jdk.CollectionConverters.*

class WatchTestChannelJob extends Job with StrictLogging {
  private val uid = System.getenv("USER_ID")
  private val testChannel = System.getenv("TEST_CHANNEL_ID")
  private val misskey = MisskeyFactory
    .getInstanceWithOwnedAccessToken(System.getenv("SERVER_HOST"), System.getenv("TOKEN"))
  private val odaiRepository = OdaiRepository()
  private val replyService = ReplyService(misskey, odaiRepository)

  def execute(context: JobExecutionContext): Unit = {
    logger.info("channel job executed at " + java.time.LocalTime.now())
    val tests = misskey.channels().timeline(ChannelsTimelineRequest.builder()
        .channelId(testChannel)
        .limit(5)
        .build())
      .get()
    tests.foreach(n =>
      if (n.getRepliesCount == 0 &&
        n.getMentions != null &&
        n.getMentions.asScala.contains(uid) &&
        n.getUserId != uid)
        replyService.doReply(n)
    )
  }
}