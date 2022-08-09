package com.anglypascal.scalite.converters

import scala.util.matching.Regex
import sttp.client3.{HttpClientSyncBackend, basicRequest, UriContext}
import com.typesafe.scalalogging.Logger

/** Basic markdown converter using the Github API for markdown to HTML
  * conversion.
  *
  * TODO: what happens if a user wants to override this?
  */
object Markdown extends Converter:
  def ext: Regex = raw"*\.(md|markdown)".r
  def outputExt = ".html"

  private val logger = Logger("Markdown Converter")

  /** Markdown converter using Github API.
    *
    * TODO: I think it'd be better just to switch to the offline converter than
    * to check for internet issues and such
    */
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
      // TODO: more useful exception mssg
      case Left(e) =>
        logger.error(
          s"Markdown converter couldn't convert $filepath to html.\n" +
            "sttp returned the error messsage: \n" + e
        )
        str
      case Right(s) =>
        logger.debug(s"Successfully converted $filepath")
        s

  def convert(str: String): String = convert(str, "string input")

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
