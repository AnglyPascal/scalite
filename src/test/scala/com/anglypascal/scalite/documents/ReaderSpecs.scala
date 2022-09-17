package com.anglypascal.scalite.documents

import com.anglypascal.scalite.documents.Reader

import org.scalatest.flatspec.AnyFlatSpec

class ReaderSpecs extends AnyFlatSpec:
  val pD = "src/test/resources/dirs/readFrom"
  val rP = "/3.md"

  it should "read front matter and main matter properly" in {
    val o = Reader.frontMatter("", rP + pD)
    assert(o("a").getStr === Some("hello") && o("b").getStr === Some("bye"))
    assert(Reader.mainMatter(rP + pD) === "Some text to go along.")
  }
