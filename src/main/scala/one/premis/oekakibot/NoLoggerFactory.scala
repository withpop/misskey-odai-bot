package one.premis.oekakibot

import net.socialhub.logger.Logger

class NoLoggerFactory extends Logger.LoggerFactory {
  override def getLogger(clazz: Class[_]): Logger = new {
    override def trace(message: String, th: Throwable): Unit = {}

    override def trace(message: String, args: Any*): Unit = {}

    override def debug(message: String, th: Throwable): Unit = {}

    override def debug(message: String, args: Any*): Unit = {}

    override def info(message: String, th: Throwable): Unit = {}

    override def info(message: String, args: Any*): Unit = {}

    override def warn(message: String, th: Throwable): Unit = {}

    override def warn(message: String, args: Any*): Unit = {}

    override def error(message: String, th: Throwable): Unit = {}

    override def error(message: String, args: Any*): Unit = {}

    override def setLogLevel(logLevel: Logger.LogLevel): Unit = {}

    override def getLogLevel: Logger.LogLevel = Logger.LogLevel.INFO
  }
}
