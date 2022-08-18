package com.anglypascal.scalite.utils

import com.typesafe.scalalogging.Logger

import java.io.File
import scala.io.Source
import scala.util.matching.Regex

object DirectoryReader:

  private val logger = Logger("Directory reader")

  /** Read the content from the given absolute path to the file
    *
    * TODO: Symlinks? We could ask the user to specify if symlinks should be
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

  /** Recover just the filename without the exteions from a file path */
  def getFileName(filepath: String): String =
    filepath.split("/").last.split(".").headOption match
      case Some(s) => s
      case None =>
        logger.error("Couldn't find valid filename")
        ""

  /** Get the relative paths to the files inside this directory */
  def getListOfFilepaths(dir: String): Array[String] =
    getListOfFiles(dir, ".^".r).map(_.getPath)

  /** Recursively find the files inside the directory dir that don't match the
    * given regex exr
    */
  def getListOfFiles(dir: File, exr: Regex): Array[File] =
    if !dir.isDirectory then
      logger.warn(s"Path ${dir.getAbsolutePath} is not a directory")
      return Array()

    val these = dir.listFiles()
    if these == null then
      logger.warn(s"IO error while accessing ${dir.getAbsolutePath}")
      return Array()

    val good = these.filter(f => !exr.matches(f.getPath))
    good ++ good.filter(_.isDirectory).flatMap(getListOfFiles(_, exr))

  def getListOfFiles(dir: String, exr: Regex): Array[File] =
    getListOfFiles(new File(dir), exr)
