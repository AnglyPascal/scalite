package com.anglypascal.scalite.utils

import com.typesafe.scalalogging.Logger

import java.io.File
import scala.io.Source

/** TODO: Symlinks? We could ask the user to specify if symlinks should be
  * followed. Test the methods to see if some sort of filtering is required.
  */
def readFile(filepath: String): Source =
  val logger = Logger("File reader")
  try Source.fromFile(filepath)
  catch
    case fnf: java.io.FileNotFoundException =>
      logger.error(s"File at $filepath not found")
      logger.debug("" + fnf.printStackTrace)
      Source.fromString("")
    case e =>
      logger.error(e.toString)
      logger.debug("" + e.printStackTrace)
      Source.fromString("")

/** Get the paths to the files inside this directory */
def getListOfFiles(dir: String): List[String] =
  val logger = Logger("Directory reader")
  val files = new File(dir).listFiles
  if files == null then
    logger.error(s"Directory at $dir not found")
    return List()
  files.filter(_.isFile).map(_.getPath).toList

/** Recover just the filename without the exteions from a file path */
def getFileName(filepath: String): String =
  filepath.split("/").last.split(".").headOption match
    case Some(s) => s
    case None => 
      Logger("Filename reader").error("Couldn't find valid filename")
      ""

