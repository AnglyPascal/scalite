package com.anglypascal.scalite.utils

import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.Data
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.typesafe.scalalogging.Logger

import java.io.File
import scala.util.matching.Regex

object Cleaner:

  /** Clears the folder with absolute path cleanSite, keeping the files matching
    * the patterns in excludes
    *
    * @param cleanSite
    *   The absolute path to the build folder
    * @param excludes
    *   List of regex strings. If a file matches any one of these patterns, it
    *   will not be removed. The regex strings will have to be relative to the
    *   cleanSite.
    *
    * @example
    *   [".*\.txt", ".git"] will keep all the files ending with extension ".txt"
    *   and the folder .git under cleanSite. Note that
    */
  def clean(cleanSite: String, excludes: List[String]): Unit =
    val r = s"$cleanSite/(${excludes.mkString("|")})".r
    val f = new File(cleanSite)

    def fn(file: File, isDir: Boolean = false) =
      if !isDir || (file.listFiles().length == 0) then
        file.delete()
        logger.trace(s"deleted $file")

    val all = recursiveListFiles(f, r)
    val files = all.filter(!_.isDirectory)
    val dirs = all.filter(_.isDirectory).sortWith(_.getPath > _.getPath)

    files.map(fn(_))
    dirs.map(fn(_, true))

  val logger = Logger("Cleaner")

  /** Recursively find all the files under the directory f that do not match the
    * given regex exr
    */
  def recursiveListFiles(f: File, exr: Regex): Array[File] =
    if !f.isDirectory then
      logger.warn(s"the passed path ${f.getPath} is not a directory")
      return Array()

    val these = f.listFiles
    if these == null then
      logger.warn(s"there was an IO errow while processing ${f.getPath}")
      return Array()

    val good = these.filter(f => !exr.matches(f.getAbsolutePath))
    good ++ good.filter(_.isDirectory).flatMap(recursiveListFiles(_, exr))

  /** Clean the destination site according to the global configuration */
  def apply(globals: DObj): Unit =
    import com.anglypascal.scalite.data.DataExtensions.{getOrElse, getStr}

    var cleanSite =
      globals.getOrElse("base")(".") + globals.getOrElse("destination")(".")

    val excludes =
      def f(l: List[String], d: Data): List[String] =
        d.getStr match
          case Some(s) => s :: l
          case _       => l
      globals
        .getOrElse("keepFiles")(List[Data]())
        .foldLeft(List[String]())(f)

    clean(cleanSite, excludes)

@main
def cleanerTest =
  val dobj = Obj(
    "base" -> "/home/ahsan/haha",
    "destination" -> "/tata",
    "keepFiles" -> Arr(".*\\.md", ".*\\.txt")
  )
  Cleaner(DObj(dobj))
