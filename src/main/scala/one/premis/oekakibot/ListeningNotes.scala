package one.premis.oekakibot

import com.typesafe.scalalogging.StrictLogging
import misskey4j.Misskey
import misskey4j.entity.Note
import misskey4j.stream.MisskeyStream

class ListeningNotes(
    val misskey: Misskey,
    val callback: Note => Unit
) extends StrictLogging with AutoCloseable {
  private var stream: MisskeyStream = _
  openStream()

  private def openStream(): Unit = {
    this.stream = misskey.stream()
    this.stream.setOpenedCallback(() => {
      logger.info("Stream opened.")
      stream.hybridTimeline(note => callback(note))
    })
    this.stream.setClosedCallback(isRemote => {
      logger.warn("Stream closed. remote: " + isRemote)
      this.openStream()
    })
    this.stream.setErrorCallback(e => {
      logger.error("Error on stream", e)
      if(!this.stream.isOpen) this.openStream()
    })
    stream.connect()
  }

  override def close(): Unit = {
    stream.unsubscribe()
    stream.close()
  }
}
