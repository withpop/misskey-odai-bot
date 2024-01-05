package one.premis.oekakibot

import better.files.File
import com.typesafe.scalalogging.StrictLogging

import java.nio.file.{FileSystems, Paths, StandardWatchEventKinds, WatchKey}
import java.time.LocalDateTime
import scala.concurrent.Future
import scala.util.Random
import concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.*

class OdaiRepository extends StrictLogging {
  private var adj: List[String] = _
  private var verb: List[String] = _
  private var name: List[String] = _
  private var attr: List[String] = _
  reloadFromFile()

//  Future {
//    val watchService = FileSystems.getDefault.newWatchService()
//    Paths.get("./").register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
//    while (true) {
//      val key = watchService.take()
//      logger.info("catch directory event")
//
//      if(key.pollEvents().asScala.exists(_.kind() == StandardWatchEventKinds.ENTRY_MODIFY)) {
//        logger.info("reload odai text")
//        reloadFromFile()
//      }
//
//      key.reset()
//    }
//  }

  def reloadFromFile(): Unit = {
    adj = File("adj.txt").lines.toList.filterNot(s => s.isEmpty || s.isBlank)
    verb = File("verb.txt").lines.toList.filterNot(s => s.isEmpty || s.isBlank)
    name = File("name.txt").lines.toList.filterNot(s => s.isEmpty || s.isBlank)
    attr = File("attr.txt").lines.toList.filterNot(s => s.isEmpty || s.isBlank)
  }

  def addAdj(t: String): Unit = {
    File("adj.txt").appendLine(t)
    reloadFromFile()
  }

  def addVerb(t: String): Unit = {
    File("verb.txt").appendLine(t)
    reloadFromFile()
  }

  def addName(t: String): Unit = {
    File("name.txt").appendLine(t)
    reloadFromFile()
  }

  def addAttr(t: String): Unit = {
    File("attr.txt").appendLine(t)
    reloadFromFile()
  }

  def getOdai(): String = {
    val a = Random.shuffle(adj).head
    val v = Random.shuffle(verb).head
    val n = Random.shuffle(name).head
    val at = Random.shuffle(attr).take(3)

    s"""お題: $a$v$n
       |あると嬉しい要素: ${at.mkString(", ")}
       |""".stripMargin
  }

}
