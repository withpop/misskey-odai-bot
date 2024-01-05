package one.premis.oekakibot

import com.typesafe.scalalogging.StrictLogging
import misskey4j.Misskey
import misskey4j.api.request.notes.NotesCreateRequest
import misskey4j.entity.Note

import scala.jdk.CollectionConverters.*

class ReplyService(
    val misskey: Misskey,
    val odaiRepository: OdaiRepository,
) extends StrictLogging {

  val typ = List("形容詞", "動詞", "名詞", "属性")

  def doReply(note: Note): Unit = {
    if(note.getText.contains("こんにちは")) {
      misskey.notes().create(NotesCreateRequest.builder()
        .text(s"@${note.getUser.getUsername} こんにちは")
        .replyId(note.getId)
        .build()
      )
    } else if(note.getText.contains("お題")) {
      misskey.notes().create(NotesCreateRequest.builder()
        .text(s"@${note.getUser.getUsername} お題です。\n\n${odaiRepository.getOdai()}")
        .replyId(note.getId)
        .build()
      )
    } else if (note.getText.contains("追加") && typ.exists(note.getText.contains(_))) {
      val parseWords = parseWord(note.getText)

      if (note.getText.contains("形容詞")) {
        logger.info(s"add adj by ${note.getUser.getName}:" + parseWords.mkString(", "))
        parseWords.foreach(odaiRepository.addAdj)
      } else if (note.getText.contains("動詞")) {
        logger.info(s"add verb by ${note.getUser.getName}:" + parseWords.mkString(", "))
        parseWords.foreach(odaiRepository.addVerb)
      } else if (note.getText.contains("名詞")) {
        logger.info(s"add name by ${note.getUser.getName}:" + parseWords.mkString(", "))
        parseWords.foreach(odaiRepository.addName)
      } else if (note.getText.contains("属性")) {
        logger.info(s"add attr by ${note.getUser.getName}:" + parseWords.mkString(", "))
        parseWords.foreach(odaiRepository.addAttr)
      }

      misskey.notes().create(NotesCreateRequest.builder()
        .text(s"@${note.getUser.getUsername} 追加しました。\n${parseWords.mkString(", ")}")
        .replyId(note.getId)
        .build()
      )
    } else {
      misskey.notes().create(NotesCreateRequest.builder()
        .text(
          s"""@${note.getUser.getUsername} 使い方:
             | 以下のノートを @oekaki へのメンションを付けて投稿してください。
             | 言葉を追加するときは、「〜追加」の後に1行毎に区切って言葉を入力して下さい。
             | 例:
             | 形容詞追加
             | かわいい
             | かっこいい
             |
             | こんにちは → 挨拶を返します
             | お題 → お題を返信します
             | 形容詞追加 → 形容詞(かわいい、でかいなど)を追加します
             | 動詞追加 → 動詞（座っている、走っているなど）を追加します
             | 名詞追加 → 名詞（男の子、犬、猫など）を追加します
             | 属性追加 → 属性（その他なんでも）を追加します
             |""".stripMargin)
        .replyId(note.getId)
        .build()
      )
    }
  }

  def parseWord(text: String): List[String] = {
    text.lines().toList.asScala.filterNot(l => l.contains("@") || l.contains("追加") || l.isEmpty || l.isBlank).toList
  }
}
