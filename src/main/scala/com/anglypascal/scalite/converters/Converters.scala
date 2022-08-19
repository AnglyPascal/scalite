package com.anglypascal.scalite.converters

import com.typesafe.scalalogging.Logger

import scala.collection.mutable.Map

/** Companion object giving api to add new members to the set of converters
  * available to Post to render it's content
  */
object Converters:

  /** Set of all the avaiable converters. When an object implements the
    * Converter trait, it gets added to this set.
    */
  private val _converters = Map[String, Converter]()

  private val logger = Logger("Converter")

  /** Find a converter by the given filetype */
  private def findByFileType(ft: String): Option[Converter] =
    _converters.get(ft)

  def modifyExtensions(exts: Map[String, String]): Unit =
    for (ft, ext) <- exts do
      findByFileType(ft) match
        case Some(conv) => conv.setExt(ext)
        case None       => ()

  /** Private method that finds the correct converter for a given file
    *
    * @param ext
    *   the path to the file to be converted
    * @return
    *   Some(c) if c accepts filetypes matching ext None if no such converter is
    *   available
    */
  def findByExt(ext: String): Option[Converter] =
    _converters.filter(_._2.matches(ext)).headOption.map(_._2)

  /** Checks if there is a converter avaiable for thei given filepath
    * @param ext
    *   the path to the file to be converted
    * @return
    *   true if there's a converter accepting this ext; false otherwise
    */
  def hasConverter(ext: String): Boolean =
    findByExt(ext) match
      case None =>
        logger.warn(s"Converter could not be found for file $ext.")
        false
      case Some(conv) =>
        logger.debug(
          s"Converter object {} found for file $ext.",
          conv.getClass.getName
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
            s"so no conversion was made for file $filepath."
        )
        str

  /** The given converter to the converters set, mapped to its filetype. This
    * overrides previously defined converter for this filetype.
    */
  def addConverter(conv: Converter): Unit =
    _converters += ((conv.fileType, conv))


