package com.anglypascal.scalite.converters

import com.anglypascal.mustache.Mustache
import com.typesafe.scalalogging.Logger
import laika.api
import laika.format
import laika.markdown.github
import laika.parse.code

object Markdown extends Converter:
  val fileType: String = "markdown"

  setExt("md,markdown,mkd")

  def outputExt = ".html"

  private val logger = Logger("Markdown converter")

  /** Markdown converter using Laika
    *
    * TODO: need to give documentation about the sytanx highlighting
    */
  def convert(str: String, filepath: String): String =
    val transformer = api.Transformer
      .from(format.Markdown)
      .to(format.HTML)
      .using(github.GitHubFlavor, code.SyntaxHighlighting)
      .build
    transformer.transform(str) match
      case Left(e) =>
        logger.error(s"Converter couldn't convert $filepath")
        str
      case Right(s) => s

// @main
def markDownTest =
  val s = "[link]({{mustache}})"
  val t = Markdown.convert(s)
  println(t)
  println(new Mustache(t).render(Map("mustache" -> "haha")))
