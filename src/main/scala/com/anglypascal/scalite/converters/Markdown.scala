package com.anglypascal.scalite.converters

import com.anglypascal.scalite.ConverterException

import scala.util.matching.Regex
import sttp.client3._
import com.github.rjeschke.txtmark.Processor.process

/** Basic markdown converter using the Github API for markdown to HTML
  * conversion.
  *
  * TODO: what happens if a user wants to override this?
  */
object Markdown extends Converter:
  def ext: Regex = raw"*\.(md|markdown)".r
  def outputExt = ".html"

  /** Markdown converter using Github API.
    *
    * TODO: I think it'd be better just to switch to the offline converter than
    * to check for internet issues and such
    */
  def convert(str: String): Either[ConverterException, String] =
    // val s = process(str)
    // Right(s)
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

  @main
  def markdownTest = 
    val md = """
  hello this is a test for scala code

  ``` scala
  def main(args: Array[String]): Unit = {
    val t = 10
    for (i <- t) 
      println(t * 2)
  }
  ```
  """
    println(Markdown.convert(md))
