package com.anglypascal.scalite.readers

import com.anglypascal.scalite.documents.Layout

/** Reads and collects all the layouts in the _layouts directory */
class LayoutsReader(directory: String)
    extends DirectoryReader[Layout](directory):

  /** First create layout objects from the template files. On the second pass,
    * connect the layouts to their parents.
    */
  def getObjectMap: Map[String, Layout] =
    val files = getListOfFiles(directory)
    val map = files.map(f => (getFileName(f), Layout(f))).toMap
    for (s, l) <- map do l.set_parent(map)
    map

/** Companion object to allow LayoutsReader(directory) to return the map of
  * layouts
  */
object LayoutsReader:
  def apply(directory: String) = (new LayoutsReader(directory)).getObjectMap
