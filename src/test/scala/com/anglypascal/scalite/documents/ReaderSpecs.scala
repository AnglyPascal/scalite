package com.anglypascal.scalite.documents

import com.anglypascal.scalite.documents.Reader

import org.scalatest.flatspec.AnyFlatSpec

class ReaderSpecs extends AnyFlatSpec:
  val R = new Reader{
    val parentDir = "src/test/resources/dirs/readFrom"
    val relativePath: String = "/3.md"
    val rType = null
  }

  it should "read front matter and main matter properly" in {
    val o = R.frontMatter
    assert(o("a").getStr === Some("hello") && o("b").getStr === Some("bye"))
    assert(R.mainMatter === "\nSome text to go along.")
  }
