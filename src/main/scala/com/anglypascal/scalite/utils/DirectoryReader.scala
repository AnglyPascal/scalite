package com.anglypascal.scalite.utils

import com.typesafe.scalalogging.Logger

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import scala.io.Source
import scala.util.matching.Regex

import Colors.*

object DirectoryReader:

  private val logger = Logger("Directory reader")

  /** Read the content from the given absolute path to the file
    *
    * TODO: By default will follow symlinks. Should we ask the user if symlinks
    * should be read?
    */
  def readFile(filepath: String): String =
    try Source.fromFile(filepath).getLines.mkString("\n")
    catch
      case fnf: java.io.FileNotFoundException =>
        logger.error(s"${RED("\"" + filepath + "\"")} not found")
        ""
      case e =>
        logger.error(e.toString)
        ""

  /** Recover just the filename without the exteions from a file path */
  def getFileName(filepath: String): String =
    if filepath == "" || filepath.endsWith("/") || filepath.endsWith(".") then
      logger.warn(s"invalid filename: ${RED("\"" + filepath + "\"")}")
      return ""
    filepath.split('/').last.split('.').headOption match
      case Some(s) => s
      case None =>
        logger.warn(s"invalid filename: ${RED("\"" + filepath + "\"")}")
        ""

  /** Get the relative paths to the files inside this directory */
  def getListOfFilepaths(dir: String): Array[String] =
    getListOfFiles(dir, ".^".r).map(_.getPath.stripPrefix(dir))

  /** Recursively find the files inside the directory dir that don't match the
    * given regex exr
    */
  def getListOfFiles(dir: File, exr: Regex): Array[File] =
    if !dir.isDirectory then
      logger.error(s"invalid directory: ${RED(s"\"${dir.getAbsolutePath}\"")}")
      return Array()

    val these = dir.listFiles()
    if these == null then
      logger.error(
        "IO error while accessing " + RED(s"\"${dir.getAbsolutePath}\"")
      )
      return Array()

    val good = these.filter(f => !exr.matches(f.getPath))
    good ++ good.filter(_.isDirectory).flatMap(getListOfFiles(_, exr))

  def getListOfFiles(dir: String, exr: Regex): Array[File] =
    getListOfFiles(new File(dir), exr)

  private val _writeTo: String => (String, String) => Unit = base =>
    (path, render) =>
      val dir = Paths.get(base + path)
      if !Files.exists(dir) then
        logger.debug(GREEN(base + path) + " doesn't exist, creating it")
        Files.createDirectories(dir.getParent)
        Files.createFile(dir)
        Files.write(dir, render.getBytes(StandardCharsets.UTF_8))
      else logger.debug(YELLOW(base + path) + " exists, ignoring")

  var writeTo: (String, String) => Unit = _

  def apply(base: String) =
    writeTo = _writeTo(base)
