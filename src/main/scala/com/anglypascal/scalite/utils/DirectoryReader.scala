package com.anglypascal.scalite.utils

import com.typesafe.scalalogging.Logger

import java.io.File
import scala.io.Source
import scala.util.matching.Regex

object DirectoryReader:

  private val logger = Logger("Directory reader")

  /** Read the content from the given absolute path to the file
    *
    * TODO: By default will follow symlinks. Should we ask the user if symlinks
    * should be read?
    */
  def readFile(filepath: String): String =
    val logger = Logger("File reader")
    try Source.fromFile(filepath).getLines.mkString("\n")
    catch
      case fnf: java.io.FileNotFoundException =>
        logger.error(
          s"File at ${Console.RED + filepath + Console.RESET} not found"
        )
        ""
      case e =>
        logger.error(e.toString)
        ""

  /** Recover just the filename without the exteions from a file path */
  def getFileName(filepath: String): String =
    if filepath == "" || filepath.endsWith("/") || filepath.endsWith(".") then
      logger.warn(s"Couldn't find valid filename in $filepath")
      return ""
    filepath.split('/').last.split('.').headOption match
      case Some(s) => s
      case None =>
        logger.warn(s"Couldn't find valid filename in $filepath")
        ""

  /** Get the relative paths to the files inside this directory */
  def getListOfFilepaths(dir: String): Array[String] =
    getListOfFiles(dir, ".^".r).map(_.getPath.stripPrefix(dir))

  /** Recursively find the files inside the directory dir that don't match the
    * given regex exr
    */
  def getListOfFiles(dir: File, exr: Regex): Array[File] =
    if !dir.isDirectory then
      logger.error(s"Path ${dir.getAbsolutePath} is not a directory")
      return Array()

    val these = dir.listFiles()
    if these == null then
      logger.error(s"IO error while accessing ${dir.getAbsolutePath}")
      return Array()

    val good = these.filter(f => !exr.matches(f.getPath))
    good ++ good.filter(_.isDirectory).flatMap(getListOfFiles(_, exr))

  def getListOfFiles(dir: String, exr: Regex): Array[File] =
    getListOfFiles(new File(dir), exr)
