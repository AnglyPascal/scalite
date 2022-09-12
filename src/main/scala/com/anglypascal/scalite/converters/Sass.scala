package com.anglypascal.scalite.converters

import de.larsgrefer.sass.embedded.SassCompilerFactory
import sass.embedded_protocol.EmbeddedSass

import java.io.File
import scala.io.Source
import com.anglypascal.scalite.data.immutable.DObj
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.Defaults

/** TODO work on the output styles, provide customization options
  */
class Sass(
    protected val configs: DObj,
    protected val globals: DObj
) extends Converter:

  protected override val logger = Logger("Sass converter")

  def fileType: String = configs.getOrElse("fileType")("none")

  def extensions: String =
    configs.getOrElse("extensions")(Defaults.Sass.extensions)

  def outputExt: String =
    configs.getOrElse("outputExt")(Defaults.Sass.outputExt)

  def convert(str: String, filepath: String): String =
    val sassCompiler = SassCompilerFactory.bundled()
    sassCompiler.setOutputStyle(EmbeddedSass.OutputStyle.COMPRESSED)

    val source =
      globals.getOrElse("base")(Defaults.Directories.base) +
        globals.getOrElse("sassDir")(Defaults.Directories.sassDir)

    import collection.convert.ImplicitConversions.`seq AsJavaList`
    sassCompiler.setLoadPaths(List(new File(source)))

    val css = sassCompiler.compileScssString(str).getCss()
    sassCompiler.close()
    css

object Sass extends ConverterConstructor:
  val constructorName: String = "sass"
  def apply(configs: DObj, globals: DObj) =
    new Sass(configs, globals)
