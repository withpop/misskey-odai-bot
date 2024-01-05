package one.premis.oekakibot

import com.typesafe.scalalogging.StrictLogging
import org.quartz.{Job, JobExecutionContext}

class HeartBeatJob extends Job with StrictLogging {
  def execute(context: JobExecutionContext): Unit = {
    logger.info("heartbeat job executed at " + java.time.LocalTime.now())
  }
}