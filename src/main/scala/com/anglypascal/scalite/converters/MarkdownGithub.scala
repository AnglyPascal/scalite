package com.anglypascal.scalite.converters

import com.anglypascal.scalite.plugins.Plugin
import com.typesafe.scalalogging.Logger
import sttp.client3.HttpClientSyncBackend
import sttp.client3.UriContext
import sttp.client3.basicRequest
import com.anglypascal.scalite.data.Data
import com.anglypascal.scalite.data.DObj
import java.net.URLEncoder

/** Basic Markdown to HTML converter using the Github API */
object MarkdownGithub extends Converter:

  val fileType: String = "markdown"

  setExt("md,markdown,mkd")

  def outputExt = ".html"

  private val logger = Logger("Markdown converter")

  def decode(string: String, otag: String, ctag: String): String =
    val o = URLEncoder.encode(otag, "UTF-8")
    val c = URLEncoder.encode(ctag, "UTF-8")
    string.replaceAll(s"$o(.*?)$c", "{{$1}}")

  /** Markdown converter using Github API. */
  def convert(str: String, filepath: String): String =
    val backend = HttpClientSyncBackend()
    val response = basicRequest
      .body(str)
      .post(uri"https://api.github.com/markdown/raw")
      .header("Content-Type", "text/plain")
      .header("Charset", "UTF-8")
      .send(backend)
      .body

    response match
      case Left(err) =>
        logger.error(
          s"Markdown converter couldn't convert $filepath to html.\n" +
            "sttp returned the error messsage: \n" + err
        )
        str // return unconverted text
      case Right(convertedText) =>
        logger.debug(s"Successfully converted $filepath")
        decode(convertedText, "{{", "}}")
