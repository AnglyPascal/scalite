package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec

class DirectoryReaderSpecs extends AnyFlatSpec:

  import DirectoryReader.*
  val root = "src/test/resources/dirs/readFrom"

  behavior of "getListOfFilePaths"
  it should "list all nested files from directory" in {
    val files = getListOfFilepaths(root + "")
    assert(files.length === 23)
  }

  behavior of "getFileName"
  it should "read valid filenames properly" in {
    val f1 = getFileName("/dir/file.ext")
    val f2 = getFileName("/file.ext")
    val f3 = getFileName("file.ext")
    val f4 = getFileName("file")
    val f5 = getFileName("fi-le.tar.xz")
    assert(
      f1 === "file" &&
        f2 === "file" &&
        f3 === "file" &&
        f4 === "file" &&
        f5 === "fi-le"
    )
  }

  it should "not read invalid filenames" in {
    val f1 = getFileName("")
    val f2 = getFileName("/dir/")
    val f3 = getFileName("/dir/file.")
    assert(f1 === "" && f2 === "" && f3 === "")
  }

  behavior of "getListOfFiles"

  it should "return empty array for invalid path" in {
    val files = getListOfFiles(root + "1", "".r)
    assert(files.length === 0)
  }

  it should "list all files if regex is null" in {
    val files = getListOfFiles(root, "".r)
    assert(files.length === 23)
  }

  it should "list files not excluded by single file matching regex" in {
    val f1 =
      getListOfFiles(root + "", ".*1.txt".r)
    assert(f1.length === 19)
    val f2 =
      getListOfFiles(root + "", ".*/subDir3/1.txt".r)
    assert(f2.length === 22)
  }

  it should "list files not excluded by single directory matching regex" in {
    val f1 =
      getListOfFiles(root + "", ".*/subDir3.*".r)
    assert(f1.length === 17)
  }

  it should "list files not excluded by multiple file matching regex" in {
    val f1 =
      getListOfFiles(root + "", raw".*1.txt|.*.2.scala".r)
    assert(f1.length === 15)
  }

  behavior of "readFile"
  it should "read existing files properly" in {
    val s1 = readFile(root + "/1.txt")
    assert(s1.getLines.mkString("\n") == "hello world!")
  }

  it should "read from symlinks properly" in {
    val s1 = readFile(root + "/../1.txt")
    assert(s1.getLines.mkString("\n") == "hello world!")
  }

  it should "return empty string for non-existing files" in {
    val s1 = readFile(root + "/2.txt")
    assert(s1.getLines.mkString("\n") == "")
  }
