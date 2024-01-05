import com.typesafe.scalalogging.StrictLogging
import misskey4j.MisskeyFactory
import misskey4j.api.request.i.IRequest
import net.socialhub.logger.Logger
import one.premis.oekakibot.*
import org.quartz.impl.StdSchedulerFactory
import org.quartz.{CronScheduleBuilder, JobBuilder, TriggerBuilder}

import java.util.TimeZone
import scala.jdk.CollectionConverters.*

object Main extends App with StrictLogging {
  val uid = System.getenv("USER_ID")

  Logger.setLoggerFactory(NoLoggerFactory())
  val misskey = MisskeyFactory
    .getInstanceWithOwnedAccessToken(System.getenv("SERVER_HOST"), System.getenv("TOKEN"))
  val response = misskey.accounts.i(IRequest.builder.build)
  logger.info("start with user: " + response.get().getUsername + " / " + response.get().getName)

  val odaiRepository = OdaiRepository()
  val replyService = ReplyService(misskey, odaiRepository)
  val notes = ListeningNotes(misskey, note => {
    if (note.getMentions != null && note.getMentions.asScala.contains(uid) && note.getUserId != uid) {
      logger.info("GOT NOTE ")
      logger.info("text : " + note.getText)
      logger.info("from user: " + note.getUserId)
      logger.info("user: " + note.getUser.getUsername)
      Thread.sleep(1000)
      replyService.doReply(note)
    }
  })
  scala.sys.addShutdownHook {
    notes.close()
  }

  val scheduler = StdSchedulerFactory.getDefaultScheduler
  val noteTrigger = TriggerBuilder.newTrigger
    .withIdentity("noteTrigger", "group")
    .withSchedule(
      CronScheduleBuilder
        .cronSchedule("0 0 20 ? * WED,SAT")
        .inTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))
    ).build
  val heartbeatTrigger = TriggerBuilder.newTrigger
    .withIdentity("heartbeatTrigger", "group")
    .withSchedule(
      CronScheduleBuilder
        //.cronSchedule("0 0 20 ? * WED,SAT")
        .cronSchedule("0 * * * * ?")
        .inTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))
    ).build
  val watchTestChannelTrigger = TriggerBuilder.newTrigger
    .withIdentity("watchTestChannelTrigger", "group")
    .withSchedule(
      CronScheduleBuilder
        .cronSchedule("*/10 * * * * ?")
        .inTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))
    ).build

  val noteJob = JobBuilder.newJob(classOf[NoteJob]).withIdentity("noteJob", "group").build
  val heartBeatJob = JobBuilder.newJob(classOf[HeartBeatJob]).withIdentity("heartbeatJob", "group").build
  val watchTestChannelJob = JobBuilder.newJob(classOf[WatchTestChannelJob]).withIdentity("watchTestChannelJob", "group").build
  scheduler.scheduleJob(heartBeatJob, heartbeatTrigger)
  scheduler.scheduleJob(noteJob, noteTrigger)
  scheduler.scheduleJob(watchTestChannelJob, watchTestChannelTrigger)
  scheduler.start()
  scala.sys.addShutdownHook {
    scheduler.shutdown()
  }
}

