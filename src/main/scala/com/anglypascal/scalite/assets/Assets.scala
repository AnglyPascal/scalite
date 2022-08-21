package com.anglypascal.scalite.assets

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.rallyhealth.weejson.v1.Obj

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

case class Asset(val filepath: String, private val globals: DObj):
  /** */
  val fileName: String = getFileName(filepath)

  val fileType: String = Files.probeContentType(Paths.get(filepath))

  val destination: String =
    globals.getOrElse("destination")(Defaults.Directories.destination) +
      globals.getOrElse("assetsDir")(Defaults.Directories.assetsDir)

  val modifiedTime: String = lastModifiedTime(fileName, Defaults.dateFormat)

  def copy(): Unit =
    val d1 = new File(filepath).toPath
    val d2 = new File(destination).toPath
    Files.copy(d1, d2, StandardCopyOption.REPLACE_EXISTING)

object Assets:

  /** Read all the asset files from directory and return a DObj holding metadata
    * for them
    */
  def apply(directory: String): Obj = ???

  /** TODO: What about online assets? */
