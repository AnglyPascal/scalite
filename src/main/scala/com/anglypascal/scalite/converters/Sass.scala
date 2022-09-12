package com.anglypascal.scalite.converters

import de.larsgrefer.sass.embedded.SassCompilerFactory
import sass.embedded_protocol.EmbeddedSass

import java.io.File
import scala.io.Source

/** TODO work on the output styles, provide customization options
  */
class Sass(
    val fileType: String,
    val extensions: String,
    val outputExt: String
) extends Converter:

  def convert(str: String, filepath: String): String =
    val sassCompiler = SassCompilerFactory.bundled()
    sassCompiler.setOutputStyle(EmbeddedSass.OutputStyle.COMPRESSED);
    val css = sassCompiler.compileFile(new File(filepath)).getCss()
    css

object Sass extends ConverterConstructor:
  val constructorName: String = "sass"
  def apply(fileType: String, extensions: String, outputExt: String) =
    new Sass(fileType, extensions, outputExt)
