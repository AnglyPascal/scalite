package com.anglypascal.scalite.assets

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.utils.StringProcessors.slugify
import com.rallyhealth.weejson.v1.Obj

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import scala.collection.mutable.Set

/** TODO: How to add metadata to assets?
  *   - Maybe the configs will define a new Asset implementation?
  *   - We can definitely do this for the defaults?
  *   - That will accept strings from a particular directory, and use it's
  *     internal settings
  */
case class Asset(val filepath: String, val destDir: String):
  /** */
  lazy val fileName: String = getFileName(filepath)

  lazy val fileType: String = Files.probeContentType(Paths.get(filepath))

  lazy val modifiedTime: String =
    lastModifiedTime(fileName, Defaults.dateFormat)

  val destination = destDir + filepath.split("/").last

  lazy val locals = Obj(
    "filepath" -> filepath,
    "fileName" -> fileName,
    "fileNameSlug" -> slugify(fileName, "default", true),
    "fileType" -> fileType,
    "modifiesTime" -> modifiedTime
  )

  def copy(): Unit =
    val d1 = new File(filepath).toPath
    val d2 = new File(destination).toPath
    Files.copy(d1, d2, StandardCopyOption.REPLACE_EXISTING)

object Assets:

  private val assets = Set[Asset]()

  /** Read asset files from directory and return metadata stored in Obj */
  def apply(from: String, to: String): Obj =
    val files = getListOfFilepaths(from)
    val obj = Obj()
    files
      .map(f => Asset(f, to))
      .foreach(a => { obj(a.fileName) = a.locals; assets += a })
    obj

  /** Copy all the assets in this object */
  def copy(): Unit = assets.map(_.copy())

  /** TODO: What about online assets? */
