package com.anglypascal.scalite.readers

import java.io.File
// import scala.collection.mutable.LinkedHashMap

class DirectoryReader[A](directory: String):
  /** */
  def getListOfFiles(dir: String): Array[String] =
    val file = new File(dir)
    file.listFiles.filter(_.isFile).map(_.getPath)

  def getFileName(filename: String): String =
    filename.split("/").last.split(".").head

  // def getObjectMap: Map[String, A]
