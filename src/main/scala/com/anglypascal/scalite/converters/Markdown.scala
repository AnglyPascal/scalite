package com.anglypascal.scalite.converters

import com.anglypascal.scalite.{Converter, ConverterException}

import scala.util.matching.Regex
import sttp.client3._

/** Basic markdown converter */
object Markdown extends Converter:
  def ext: Regex = raw"*\.(md|markdown)".r
  def outputExt = ".html"

  /** Markdown converter using Github API. 
   *
   *  TODO: I think it'd be better just to switch to the offline converter than to
   *  check for internet issues and such
   */
  def convert(str: String): Either[ConverterException, String] =
    val backend = HttpClientSyncBackend()
    val response = basicRequest
      .body(str)
      .post(uri"https://api.github.com/markdown/raw")
      .header("Content-Type", "text/plain")
      .header("Charset", "UTF-8")
      .send(backend)
      .body

    response match
      // TODO: more useful exception mssg
      case Left(_) =>
        Left(ConverterException("couldn't convert markdown to html"))
      case Right(s) => Right(s)
