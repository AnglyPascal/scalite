package com.anglypascal.scalite.readers

import com.anglypascal.scalite.Layout

class LayoutsReader(directory: String)
    extends DirectoryReader[Layout](directory):
  /** */
  def getObjectMap: Map[String, Layout] =
    val files = getListOfFiles(directory)
    val map = files.map(f => (getFileName(f), new Layout(f))).toMap
    for (s, l) <- map do l.set_parent(map)
    map

object LayoutsReader:
  def apply(directory: String) = (new LayoutsReader(directory)).getObjectMap
