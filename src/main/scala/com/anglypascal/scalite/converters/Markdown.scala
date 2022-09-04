package com.anglypascal.scalite.converters

import com.anglypascal.mustache.Mustache
import com.typesafe.scalalogging.Logger
import laika.api
import laika.format
import laika.markdown.github
import laika.parse.code

/** Markdown converter using Laika
  *
  * TODO: documentation about the sytanx highlighting
  */
class Markdown(
    val fileType: String,
    val extensions: String,
    val outputExt: String
) extends Converter:

  private val logger = Logger("Markdown converter")

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

object Markdown extends ConverterConstructor:
  val constructorName: String = "markdown"
  def apply(fileType: String, extensions: String, outputExt: String) =
    new Markdown(fileType, extensions, outputExt)
