package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.{getListOfFiles, getFileName}
import com.typesafe.scalalogging.Logger

/** Defines methods to process the partial layouts in "/\_includes" folder
  */
object Partial:

  /** Prevent accessing the partials before apply is called
    */
  private var _partials: Map[String, Layout] = _
  def partials = _partials

  private val logger = Logger("Partial object")

  /** Process all the partials in "/\_partials" directory. This is done in two
    * passes. The first pass creates the partials without specifying the
    * parents. The second pass assigns to each partial it's parent, if specified
    * in the front matter.
    */
  def apply(directory: String): Map[String, Layout] =
    val files = getListOfFiles(directory)
    val ls = files
      .map(f => {
        val fn = getFileName(f)
        (fn, new MustacheLayout(getFileName(f), f))
      })
      .toMap

    logger.debug("Got the partials: " + ls.map(_._2.name).mkString(", "))
    ls.map((s, l) => l.setParent(ls))
    _partials = ls.toMap
    partials
