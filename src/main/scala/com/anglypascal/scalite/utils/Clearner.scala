package com.anglypascal.scalite.utils

import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.data.immutable.Data
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFiles
import com.typesafe.scalalogging.Logger

import java.io.File
import scala.util.matching.Regex

/** Cleans the build directory before write files to it */
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
  private def clean(cleanSite: String, excludes: List[String]): Unit =
    val r = s"(${excludes.mkString("|")})".r
    // delete a path if it's a file, or the directory is empty
    def fn(file: File, isDir: Boolean = false) =
      if !isDir || (file.listFiles().length == 0) then
        file.delete()
        logger.trace(s"deleted $file")
    // paths relative to the cleanSite
    val all = getListOfFiles(cleanSite, r)
    val files = all.filter(!_.isDirectory)
    val dirs = all.filter(_.isDirectory).sortWith(_.getPath > _.getPath)
    // delete the files and empty directories
    files.map(fn(_))
    dirs.map(fn(_, true))

  private val logger = Logger("Cleaner")

  /** Clean the destination site according to the global configuration */
  def apply(globals: DObj): Unit =

    var cleanSite = // FIXME default would not be the absolute path though
      globals.getOrElse("base")(".") +
        globals.getOrElse("destination")("/_site")

    val excludes =
      def f(l: List[String], d: Data): List[String] =
        d.getStr match
          case Some(s) => s :: l
          case _       => l
      globals
        .getOrElse("keepFiles")(List[Data]())
        .foldLeft(List[String]())(f)

    clean(cleanSite, excludes)
