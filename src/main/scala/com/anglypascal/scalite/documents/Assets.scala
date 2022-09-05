package com.anglypascal.scalite.documents

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.slugify

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import scala.collection.mutable.Set
import com.anglypascal.scalite.ScopedDefaults

case class Asset(val filepath: String, val destDir: String):
  /** */

  private val scopedDefaults = ScopedDefaults.getDefaults(filepath, "asset")

  lazy val fileName: String = getFileName(filepath)

  lazy val fileType: String = Files.probeContentType(Paths.get(filepath))

  lazy val modifiedTime: String =
    lastModifiedTime(fileName, Defaults.dateFormat)

  val destination = destDir + filepath.split("/").last

  lazy val locals = MObj(
    "filepath" -> filepath,
    "fileName" -> fileName,
    "fileNameSlug" -> slugify(fileName, "default", true),
    "fileType" -> fileType,
    "modifiesTime" -> modifiedTime
  ) update scopedDefaults

  def copy(): Unit =
    val d1 = new File(filepath).toPath
    val d2 = new File(destination).toPath
    Files.copy(d1, d2, StandardCopyOption.REPLACE_EXISTING)

object Assets:

  private val assets = Set[Asset]()

  /** Read asset files from directory and return metadata stored in Obj */
  def apply(from: String, to: String): MObj =
    val files = getListOfFilepaths(from)
    val obj = MObj()
    files
      .map(f => Asset(f, to))
      .foreach(a => { obj(a.fileName) = a.locals; assets += a })
    obj

  /** Copy all the assets in this object */
  def copy(): Unit = assets.map(_.copy())

  /** TODO: What about online assets? */
