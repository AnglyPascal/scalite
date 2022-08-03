package com.anglypascal.scalite.readers

import java.io.File

/** Trait to collect all the files in a directory in a map of specified objects
 *
 *  TODO: test the methods to see if some sort of filtering is required. Also symlinks
 *  could cause problems. 
  */
trait DirectoryReader[A](directory: String):

  /** Get the paths to the files inside this directory */
  def getListOfFiles(dir: String): Array[String] =
    val file = new File(dir)
    file.listFiles.filter(_.isFile).map(_.getPath)

  /** Recover just the filename without the exteions from a file path */
  def getFileName(filename: String): String =
    filename.split("/").last.split(".").head
