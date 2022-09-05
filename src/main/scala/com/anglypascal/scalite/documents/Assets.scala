package com.anglypascal.scalite.documents

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.slugify
import com.typesafe.scalalogging.Logger

import java.io.File
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import scala.collection.mutable.Set

class Asset(val filepath: String, val destDir: String):
  /** */
  private val logger = Logger("Asset")

  private val scopedDefaults = ScopedDefaults.getDefaults(filepath, "asset")

  lazy val fileName: String = getFileName(filepath)

  private lazy val fileType: String =
    Files.probeContentType(Paths.get(filepath))

  private lazy val modifiedTime: String =
    lastModifiedTime(fileName, Defaults.dateFormat)

  private val destination = destDir + filepath.split("/").last

  lazy val locals = MObj(
    "filepath" -> filepath,
    "fileName" -> fileName,
    "fileNameSlug" -> slugify(fileName, "default", true),
    "fileType" -> fileType,
    "modifiesTime" -> modifiedTime
  ) update scopedDefaults

  def copy(dryRun: Boolean = false, replace: Boolean = true): Unit =
    if !dryRun then
      val d1 = new File(filepath).toPath
      val d2 = new File(destination).toPath
      if replace then
        logger.debug(s"copying $filepath to $destination, replacing if exists")
        try
          import StandardCopyOption.*
          Files.copy(d1, d2, REPLACE_EXISTING, COPY_ATTRIBUTES)
        catch
          case e =>
            logger.debug(s"$e ocurred while copying $filepath to $destination")
      else
        try
          Files.copy(d1, d2, StandardCopyOption.COPY_ATTRIBUTES)
          logger.debug(s"copying $filepath to $destination")
        catch
          case e: FileAlreadyExistsException =>
            logger.debug(s"$destination exists, and not replacing")
          case e =>
            logger.debug(s"$e ocurred while copying $filepath to $destination")
    else logger.debug(s"would copy $filepath to $destination")

  /** TODO: What about online assets? */

object Assets:

  private val assets = Set[Asset]()

  private var replace: Boolean = _

  /** Read asset files from directory and return metadata stored in Obj */
  def apply(from: String, to: String, _replace: Boolean = true): MObj =
    replace = _replace
    val files = getListOfFilepaths(from)
    val obj = MObj()
    files
      .map(f => Asset(f, to))
      .foreach { a => { obj += a.fileName -> a.locals; assets += a } }
    obj

  /** Copy all the assets in this object */
  def copy(dryRun: Boolean = false): Unit =
    assets foreach { _.copy(dryRun, replace) }
