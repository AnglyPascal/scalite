package com.anglypascal.scalite.layouts

import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.plugins.Plugin

/** A trait for generic layout object. Specifies which files this layout will
  * match, and how it will create layouts from the files in the given
  * directories.
  */
trait LayoutGroup:

  val lang: String

  val layoutsDir: String

  val partialsDir: String

  def partialFiles = getListOfFilepaths(partialsDir)

  def layoutFiles = getListOfFilepaths(layoutsDir)

  /** Get the layouts of this type */
  def layouts: Map[String, Layout]

  /** The extensions of the files this converter is able to convert */
  val ext: String

  /** Does this constructor recognize this filepath? */
  def matches(filepath: String): Boolean =
    val re =
      "(" + ext.split(",").map(_.trim).map(".*\\." + _).mkString("|") + ")"
    re.r.matches(filepath)

trait LayoutGroupConstructor extends Plugin:

  val lang: String

  def apply(layoutsDir: String, partialsDir: String, ext: String): LayoutGroup
