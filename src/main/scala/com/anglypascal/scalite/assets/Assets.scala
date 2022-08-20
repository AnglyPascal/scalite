package com.anglypascal.scalite.assets

import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import java.nio.file.Files
import java.nio.file.Paths
import com.anglypascal.scalite.data.DObj
import com.rallyhealth.weejson.v1.Obj

abstract case class Asset(val filepath: String, private val globals: DObj):
  /** */
  val fileName: String = getFileName(filepath)

  val fileType: String = Files.probeContentType(Paths.get(filepath))

  val destination: String

  val modifiedTime: String = ???

  def copy(): Unit = ???

object Assets:

  /** Read all the asset files from directory and return a DObj holding metadata
    * for them
    */
  def apply(directory: String): Obj = ???

  /** TODO: What about online assets? */
