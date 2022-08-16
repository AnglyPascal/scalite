package com.anglypascal.scalite.utils

import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.converters.Converter

object StringProcessors:

  /** during the rendering of a post, (or potentially anything), fetch the first
    * paragraph and return an excerpt.
    */
  def excerpt(str: String, separator: String = "\n\n"): String = 
    str.split(separator).headOption match
      case Some(head) => 
        head
      case None =>
        logger.warn("method excerpt received an empty string")
        ""

  private val slugifyModes =
    List("none", "raw", "default", "pretty", "ascii", "latin")

  private val rawRegex = raw"\s+"
  private val defaultRegex = raw"[^\p{IsLatin}\p{L}\p{Nd}]+"
  private val prettyRegex = raw"[^\p{IsLatin}\p{L}\p{Nd}._~!&'()+,;=@]+"
  private val asciiRegex = raw"[^[A-Za-z0-9]]+"

  private val logger = Logger("String processor")

  /** Slugify a filename or title.
    *
    * @param string
    *   the filename or title to slugify
    * @param mode
    *   how string is slugified
    *
    *   - '''none''': returns the given string.
    *   - '''raw''': returns the given string, with every sequence of spaces
    *     characters replaced with a hyphen.
    *   - '''default''': non-alphabetic characters are replaced with a hyphen
    *     too.
    *   - '''pretty''': some non-alphabetic characters (.\_~!$&'()+,;=@) are not
    *     replaced with hyphen.
    *   - '''ascii''': everything except ASCII characters a-z (lowercase), A-Z
    *     (uppercase) and 0-9 (numbers) are replaced with hyphen.
    *   - '''latin''': the input string is first preprocessed so that any
    *     letters with accents are replaced with the plain letter. Afterwards,
    *     it follows the "default" mode of operation.
    *
    * @param cased
    *   whether to replace all uppercase letters with their lowercase
    *   counterparts
    *
    * If cased is true, all uppercase letters in the result string are replaced
    * with their lowercase counterparts.
    *
    * @returns
    *   the slugified string.
    *
    * Examples:
    * {{{
    *   slugify("The _config.yml file")
    *   # => "the-config-yml-file"
    *
    *   slugify("The _config.yml file", "pretty")
    *   # => "the-_config.yml-file"
    *
    *   slugify("The _config.yml file", "pretty", true)
    *   # => "The-_config.yml file"
    *
    *   slugify("The _config.yml file", "ascii")
    *   # => "the-config-yml-file"
    *
    *   slugify("The _config.yml file", "latin")
    *   # => "the-config-yml-file"
    * }}}
    */
  def slugify(
      str: String,
      mode: String = "default",
      cased: Boolean = false
  ): String =
    if !slugifyModes.contains(mode) then
      logger.error(
        "Slugify mode has to be an element of " +
          s"[${slugifyModes.mkString(", ")}]. Given mode: $mode."
      )
      ""
    else
      val uncased = mode match
        case "none"    => str
        case "raw"     => str.replaceAll(rawRegex, "-")
        case "default" => str.replaceAll(defaultRegex, "-")
        case "pretty"  => str.replaceAll(prettyRegex, "-")
        case "ascii"   => str.replaceAll(asciiRegex, "-")
        case "latin" =>
          str.replaceAll("\\p{M}", "").replaceAll(defaultRegex, "-")
        case _ =>
          logger.error(
            "Slugify mode has to be an element of " +
              s"[${slugifyModes.mkString(", ")}]. Given mode: $mode."
          )
          ""
      if cased then uncased
      else uncased.toLowerCase

  /** Turns a slug into a simple title */
  def titlify(slug: String, allCaps: Boolean = true): String =
    if allCaps then slug.split('-').map(_.capitalize).mkString(" ")
    else slug.split('-').mkString(" ").capitalize

  @main
  def stringProcessor =
    val s = "Import _config.yml"
    slugifyModes.map(m => m -> slugify(s, m)).map(println(_))
    slugifyModes.map(slugify(s, _, true)).map(println(_))
