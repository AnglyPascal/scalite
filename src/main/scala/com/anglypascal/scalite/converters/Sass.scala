package com.anglypascal.scalite.converters

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.hooks.CollectionAfterWrite
import com.anglypascal.scalite.hooks.Hooks
import com.typesafe.scalalogging.Logger
import de.larsgrefer.sass.embedded.SassCompilerFactory
import sass.embedded_protocol.EmbeddedSass
import sass.embedded_protocol.EmbeddedSass.OutputStyle

import java.io.File
import scala.collection.convert.ImplicitConversions.`seq AsJavaList`
import scala.collection.mutable.LinkedHashMap
import scala.io.Source

/** Sass converter, uses the Java implementation of the Sass embedded protocol
  * in https://github.com/larsgrefer/dart-sass-java
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

  private lazy val sassCompiler =
    val style: OutputStyle =
      val styles = LinkedHashMap[String, OutputStyle](
        "compressed" -> OutputStyle.COMPRESSED,
        "expanded" -> OutputStyle.EXPANDED,
        "unrecognized" -> OutputStyle.UNRECOGNIZED
      )
      val s = configs.getOrElse("outputStyle")(Defaults.Sass.outputStyle)
      styles.get(s) match
        case Some(o) => o
        case None    => OutputStyle.COMPRESSED

    val sc = SassCompilerFactory.bundled()
    val source =
      globals.getOrElse("base")(Defaults.Directories.base) +
        globals.getOrElse("sassDir")(Defaults.Directories.sassDir)
    sc.setOutputStyle(style)
    sc.setLoadPaths(List(new File(source)))

    /** TODO: Implement as Hooks */
    // sc.registerFunction()
    sc

  def convert(str: String, filepath: String): String =
    try
      if filepath.endsWith(".sass") then
        logger.debug(s"using sass converter for $filepath")
        sassCompiler.compileSassString(str).getCss()
      else
        logger.debug(s"using scss converter for $filepath")
        sassCompiler.compileScssString(str).getCss()
    catch
      case e =>
        logger.error(s"Converting $filepath resulted in error ${e.toString}")
        ""
  override def convert(str: String): String = convert(str, "stringInput.scss")

  private def close(): Unit = sassCompiler.close()

  private object SassCloseHook extends CollectionAfterWrite:
    val priority: Int = 0
    def apply(_globals: DObj)(obj: Collection): Unit =
      if obj.name == "sass" then close()

  Hooks.registerHook(SassCloseHook)

object Sass extends ConverterConstructor("sass"):
  def apply(configs: DObj, globals: DObj) =
    new Sass(configs, globals)
