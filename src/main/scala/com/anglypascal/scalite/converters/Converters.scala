package com.anglypascal.scalite.converters

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.plugins.ConverterHooks

/** Holds all implementations of Converter and ConverterConstructor.
  *
  * It's a Configurable, it looks out for "converters" section in \_config.yml
  * for configurations to add to itself.
  *
  * Based on these configurations, creates a hash table holding filetype ->
  * converter maps.
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

  /** The given converter to the converters set, mapped to its filetype. This
    * overrides previously defined converter for this filetype.
    */
  def addConverterConstructor(conv: ConverterConstructor): Unit =
    converterConstructors += conv.constructorName -> conv

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
    ),
    "sass" -> MObj(
      "converter" -> "sass",
      "extensions" -> Defaults.Sass.extensions,
      "outputExt" -> Defaults.Sass.outputExt
    )
  )

  def apply(configs: MObj, globals: IObj): Unit =
    convsConfig update configs

    for (name, conv) <- convsConfig do
      conv match
        case conv: MObj =>
          val cn = conv.getOrElse("converter")("identity")
          val ex = conv.getOrElse("extensions")("")
          val oe = conv.getOrElse("outputExt")(".html")
          val C = converterConstructors.get(cn).getOrElse(Identity)

          logger.debug(
            s"new converter: filetype: ${GREEN(name)}, " +
              s"extensions: ${GREEN(ex.split(",").map(_.trim).mkString(", "))}, " +
              s"outputExt: ${GREEN(oe)}, " +
              s"converter constructor: ${BLUE(cn)}"
          )
          ConverterHooks.beforeInits foreach { _.apply(name, IObj(conv)) }
          converters += name -> C(name, ex, oe)
        case _ =>
          logger.debug(s"please provide configs for convert $name as a table")

  /** Private method that finds the correct converter for a given file
    *
    * @param ext
    *   the path to the file to be converted
    * @return
    *   Some(c) if c accepts filetypes matching ext None if no such converter is
    *   available
    */
  private def findByExt(ext: String): Option[Converter] =
    converters.filter(_._2.matches(ext)).headOption.map(_._2)

  def findOutputExt(ext: String): String =
    findByExt(ext).map(_.outputExt).getOrElse(".html")

  /** Checks if there is a converter avaiable for thei given filepath
    * @param ext
    *   the path to the file to be converted
    * @return
    *   true if there's a converter accepting this ext; false otherwise
    */
  def hasConverter(ext: String): Boolean =
    findByExt(ext) match
      case None =>
        logger.debug(s"Converter not found for ${RED(ext)}")
        false
      case Some(conv) =>
        logger.debug(
          s"${GREEN(conv.getClass.getSimpleName.stripSuffix("$"))} " +
            s"found for ${GREEN(ext)}"
        )
        true

  /** Convert a given filepath with an appropriate converter if it exists.
    *
    * It applies all the filters from ConverterHooks.beforeConverts to the input
    * string and ConverterHooks.afterConverts to the output string, applying them in
    * order of decreasing priority.
    *
    * @param str
    *   the string to be converted. In our case, the main matter of the file
    * @param filepath
    *   the path to the file containing the extension. Used to find appropriate
    *   converter
    * @returns
    *   The converted string returned by the first converter found for the
    *   filepath
    */
  def convert(str: String, filepath: String): String =
    val nstr = findByExt(filepath) match
      case Some(converter) =>
        val ns = ConverterHooks.beforeConverts
          .foldLeft(str)((s, h) => h.apply(s, filepath))
        converter.convert(ns, filepath)
      case None =>
        logger.warn("no converter found for " + RED(filepath))
        ConverterHooks.beforeConverts
          .foldLeft(str)((s, h) => h.apply(s, filepath))
    ConverterHooks.afterConverts.foldLeft(nstr)((s, h) => h.apply(s, filepath))

  override def toString(): String =
    converters.map("  " + _._2.toString).mkString("\n")
