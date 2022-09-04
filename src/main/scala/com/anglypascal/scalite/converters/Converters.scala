package com.anglypascal.scalite.converters

import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.utils.Colors.*

/** Companion object giving api to add new members to the set of converters
  * available to Post to render it's content
  */
object Converters extends Configurable:

  val sectionName: String = "converters"

  /** Set of all the avaiable converters. When an object implements the
    * Converter trait, it gets added to this set. Each converter is mapped to
    * the filetype it converts.
    */
  private val converters = LinkedHashMap[String, Converter]()

  private val converterConstructors =
    LinkedHashMap[String, ConverterConstructor](
      "markdown" -> Markdown,
      "markdownGithub" -> MarkdownGithub,
      "identity" -> Identity
    )

  private val logger = Logger("Converter")

  private val convsConfig = MObj(
    "markdown" -> MObj(
      "converter" -> "markdown",
      "extensions" -> Defaults.Markdown.extensions,
      "outputExt" -> Defaults.Markdown.outputExt
    ),
    "html" -> MObj(
      "converter" -> "identity",
      "extensions" -> Defaults.Identity.extensions,
      "outputExt" -> Defaults.Identity.outputExt
    )
  )

  def apply(configs: MObj, globals: IObj): Unit =
    convsConfig.update(configs)
    for (name, conv) <- convsConfig do
      conv match
        case conv: MObj => 
          val cn = conv.getOrElse("converter")("identity")
          val ex = conv.getOrElse("extensions")("")
          val oe = conv.getOrElse("outputExt")(".html")
          val C = converterConstructors.get(cn).getOrElse(Identity)
          converters += name -> C(name, ex, oe)
        case _ => ()

  /** Private method that finds the correct converter for a given file
    *
    * @param ext
    *   the path to the file to be converted
    * @return
    *   Some(c) if c accepts filetypes matching ext None if no such converter is
    *   available
    */
  def findByExt(ext: String): Option[Converter] =
    converters.filter(_._2.matches(ext)).headOption.map(_._2)

  /** Checks if there is a converter avaiable for thei given filepath
    * @param ext
    *   the path to the file to be converted
    * @return
    *   true if there's a converter accepting this ext; false otherwise
    */
  def hasConverter(ext: String): Boolean =
    findByExt(ext) match
      case None =>
        logger.warn(
          s"Converter could not be found for file ${Console.RED + ext + Console.RESET}"
        )
        false
      case Some(conv) =>
        logger.debug(
          s"${GREEN(conv.getClass.getSimpleName.stripSuffix("$"))} found for " +
            s"${GREEN(ext)}"
        )
        true

  /** Convert a given filepath with an appropriate converter if it exists
    *
    * @param str
    *   the string to be converted. In our case, the main matter of the file
    * @param filepath
    *   the path to the file containing the extension. Used to find appropriate
    *   converter
    */
  def convert(
      str: String,
      filepath: String
  ): String =
    findByExt(filepath) match
      case Some(converter) =>
        converter.convert(str, filepath)
      case None =>
        logger.warn(
          "Converter could not be found, " +
            "so no conversion was made for file " +
            Console.RED + filepath + Console.RESET
        )
        str

  def findExt(filepath: String) =
    findByExt(filepath).map(_.outputExt).getOrElse(".html")

  /** The given converter to the converters set, mapped to its filetype. This
    * overrides previously defined converter for this filetype.
    */
  def addConverterConstructor(conv: ConverterConstructor): Unit =
    converterConstructors += conv.constructorName -> conv

  override def toString(): String = 
    converters.map("  " + _._2.toString).mkString("\n")
