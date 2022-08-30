package com.anglypascal.scalite.documents

import com.anglypascal.scalite.documents.Reader

import org.scalatest.flatspec.AnyFlatSpec

class ReaderSpecs extends AnyFlatSpec:
  val R = new Reader{
    val filepath = "src/test/resources/dirs/readFrom/3.md"
    val rType = null
  }

  it should "read front matter and main matter properly" in {
    val o = R.frontMatter
    assert(o("a").str === "hello" && o("b").str === "bye")
    assert(R.main_matter === "\nSome text to go along.")
  }
