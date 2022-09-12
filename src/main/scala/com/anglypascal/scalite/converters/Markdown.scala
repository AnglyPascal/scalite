package com.anglypascal.scalite.converters

import com.anglypascal.mustache.Mustache
import com.typesafe.scalalogging.Logger
import laika.api
import laika.format
import laika.markdown.github
import laika.parse.code
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.Defaults

/** Markdown converter using Laika
  *
  * TODO: documentation about the sytanx highlighting
  */
class Markdown(
    protected val configs: DObj,
    protected val globals: DObj
) extends Converter:

  protected override val logger = Logger("Markdown converter")

  def fileType: String = configs.getOrElse("fileType")("none")

  def extensions: String =
    configs.getOrElse("extensions")(Defaults.Markdown.extensions)

  def outputExt: String =
    configs.getOrElse("outputExt")(Defaults.Markdown.outputExt)

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
  def apply(configs: DObj, globals: DObj) =
    new Markdown(configs, globals)
