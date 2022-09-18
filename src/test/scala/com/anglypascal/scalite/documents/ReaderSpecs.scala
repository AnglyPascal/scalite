package com.anglypascal.scalite.documents

import com.anglypascal.scalite.documents.Reader

import org.scalatest.flatspec.AnyFlatSpec

class ReaderSpecs extends AnyFlatSpec:

  it should "read front matter and main matter properly" in {
    val fp = "src/test/resources/dirs/readFrom/3.md"

    val o = Reader.frontMatter("", fp)
    val s = Reader.mainMatter(fp)
    assert(
      o("a").getStr === Some("hello") &&
        o("b").getStr === Some("bye") &&
        o("shouldConvert").getBool == Some(true) &&
        s === "Some text to go along."
    )
  }

  it should "read main matter properly without front matter" in {
    val fp = "src/test/resources/dirs/readFrom/1.txt"

    val o = Reader.frontMatter("", fp)
    val s = Reader.mainMatter(fp)
    assert(
        o("shouldConvert").getBool == Some(false) &&
        s === "hello world!"
    )
  }
