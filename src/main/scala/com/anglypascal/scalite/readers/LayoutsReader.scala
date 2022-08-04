package com.anglypascal.scalite.readers

import com.anglypascal.scalite.documents.Layout
import com.rallyhealth.weejson.v1.Obj

/** Reads and collects all the layouts in the _layouts directory */
class LayoutsReader(directory: String, globals: Obj)
    extends DirectoryReader[Layout](directory):

  /** First create layout objects from the template files. On the second pass,
    * connect the layouts to their parents.
    */
  def getObjectMap: Map[String, Layout] =
    val files = getListOfFiles(directory)
    val map = files.map(f => (getFileName(f), Layout(f, globals))).toMap
    for (s, l) <- map do l.setupParent(map)
    map

/** Companion object to allow LayoutsReader(directory) to return the map of
  * layouts
  */
object LayoutsReader:
  def apply(directory: String, globals: Obj) = (new LayoutsReader(directory, globals)).getObjectMap
