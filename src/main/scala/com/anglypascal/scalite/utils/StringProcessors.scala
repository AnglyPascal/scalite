package com.anglypascal.scalite.utils

import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.converters.Converter
import java.text.Normalizer

object StringProcessors:

  private inline val rawRegex = "\\s+"
  private inline val defaultRegex = "[^\\p{IsLatin}\\p{L}\\p{Nd}]+"
  private inline val prettyRegex = "[^\\p{IsLatin}\\p{L}\\p{Nd}._~!&'()+,;=@]+"
  private inline val asciiRegex = "[^[A-Za-z0-9]]+"
  private inline val slugifyModes =
    """"none", "raw", "default", "pretty", "ascii", "latin""""
  private val title = raw"\d{4}-\d{2}-\d{2}-(.*)".r

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
    *   slugify("A title _with Spaces! & other chars@procs,", "pretty")
    *   # => "a-title-_with-spaces!-&-other-chars@procs,"
    *
    *   slugify("A title _with Spaces! & other chars@procs,", "pretty", true)
    *   # => "A-title-_with-Spaces!-&-other-chars@procs,"
    *
    *   slugify("A title _with SpacÈs! & Ùther chars@procs,", "ascii")
    *   # => "a-title-with-spac-s-ther-chars-procs-"
    *
    *   slugify("A title _with SpacÈs! & Ùther chars@procs,", "latin")
    *   # => "a-title-with-spaces-uther-chars-procs-"
    * }}}
    */
  def slugify(
      str: String,
      mode: String = "default",
      cased: Boolean = false
  ): String =
    val s = mode match
      case "none"    => str
      case "raw"     => str.replaceAll(rawRegex, "-")
      case "default" => str.replaceAll(defaultRegex, "-")
      case "pretty"  => str.replaceAll(prettyRegex, "-")
      case "ascii"   => str.replaceAll(asciiRegex, "-")
      case "latin" =>
        val latin = Normalizer.normalize(str, Normalizer.Form.NFD);
        latin.replaceAll("[^\\p{ASCII}]", "").replaceAll(defaultRegex, "-")
      case _ =>
        logger.error(
          "Slugify mode has to be an element of " +
            s"[$slugifyModes]. Given mode: $str. " +
            s"Falling back to \"default\""
        )
        str.replaceAll(defaultRegex, "-")

    if cased then s
    else s.toLowerCase

  /** Turns a slug into a simple title */
  inline def titlify(slug: String, allCaps: Boolean = false): String =
    if allCaps then slug.split('-').map(_.capitalize).mkString(" ")
    else slug.split('-').mkString(" ").capitalize

  /** Parses title from the filenames with the pattern "yyyy-MM-dd-title" */
  inline def titleParser(fn: String): Option[String] =
    inline fn match
      case title(t) => Some(titlify(t))
      case _        => None

  /** Removes double backslashes and encodes the url to ascii */
  inline def purifyUrl(str: String): String =
    import io.lemonlabs.uri.Url
    val s = str.replaceAll("[/]+", "/")
    Url.parse(s).toString

  /** Quote the given string, escaping all escapable characters */
  inline def quote(s: String): String = "\"" + escape(s) + "\""
  private inline def escape(s: String): String = s.flatMap(escapedChar)

  private def escapedChar(ch: Char): String =
    ch match
      case '\b' => "\\b"
      case '\t' => "\\t"
      case '\n' => "\\n"
      case '\f' => "\\f"
      case '\r' => "\\r"
      case '"'  => "\\\""
      case '\'' => "\\\'"
      case '\\' => "\\\\"
      case _ =>
        if (ch.isControl) "\\0" + Integer.toOctalString(ch.toInt)
        else String.valueOf(ch)

  /** Pad the given string to the right by " " upto a length of num */
  inline def pad(str: Any, num: Int): String =
    val s = str.toString
    val l = s.length
    if l < num then s + (" " * (num - l))
    else s

  /** TODO: Merge two given directories */
  def mergePaths(path1: String, path2: String): String = ???
