package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec

class SlugifySpecs extends AnyFlatSpec:

  import StringProcessors.slugify

  it should "handle none mode properly" in {
    val f = "A title _with Spaces! & other chars@procs,"
    val t = f.toLowerCase
    assert(slugify(f, "none") === t)
  }

  it should "handle none mode with case properly" in {
    val f = "A title _with Spaces! & other chars@procs,"
    val t = f
    assert(slugify(f, "none", true) === t)
  }

  it should "handle default mode properly" in {
    val f = "A title _with Spaces! & other chars@procs,"
    val t = "a-title-with-spaces-other-chars-procs-"
    assert(slugify(f) === t)
  }

  it should "handle default mode with cased properly" in {
    val f = "A title _with Spaces! & other chars@procs,"
    val t = "A-title-with-Spaces-other-chars-procs-"
    assert(slugify(f, "default", true) === t)
  }

  it should "handle pretty mode properly" in {
    val f = "A title _with Spaces! & other chars@procs,"
    val t = "a-title-_with-spaces!-&-other-chars@procs,"
    assert(slugify(f, "pretty") === t)
  }

  it should "handle pretty mode with cased properly" in {
    val f = "A title _with Spaces! & other chars@procs,"
    val t = "A-title-_with-Spaces!-&-other-chars@procs,"
    assert(slugify(f, "pretty", true) === t)
  }

  it should "handle ascii mode properly" in {
    val f = "A title _with SpacÈs! & Ùther chars@procs,"
    val t = "a-title-with-spac-s-ther-chars-procs-"
    assert(slugify(f, "ascii") === t)
  }

  it should "handle ascii mode with case properly" in {
    val f = "A title _with SpacÈs! & Ùther chars@procs,"
    val t = "A-title-with-Spac-s-ther-chars-procs-"
    assert(slugify(f, "ascii", true) === t)
  }

  it should "handle latin mode properly" in {
    val f = "A title _with SpacÈs! & Ùther chars@procs,"
    val t = "a-title-with-spaces-uther-chars-procs-"
    assert(slugify(f, "latin") === t)
  }

  it should "handle latin mode with case properly" in {
    val f = "A title _with SpacÈs! & Ùther chars@procs,"
    val t = "A-title-with-SpacEs-Uther-chars-procs-"
    assert(slugify(f, "latin", true) === t)
  }

class TitlifySpecs extends AnyFlatSpec:

  import StringProcessors.titlify

  it should "convert slug to normal title  properly" in {
    val f = "this-is-a-slug"
    val t = "This is a slug"
    assert(titlify(f) === t)
  }

  it should "convert slug to all capped words title properly" in {
    val f = "this-is-a-slug"
    val t = "This Is A Slug"
    assert(titlify(f, true) === t)
  }

class TitleParserSpecs extends AnyFlatSpec:

  import StringProcessors.titleParser

  it should "read title from valid filename with date properly" in {
    val fn = "2022-08-28-this-is-a-title"
    val t = "This is a title"
    assert(titleParser(fn) === Some(t))
  }

  it should "fail to read title from invalid filename" in {
    val fn = "2022-08-2454-this-is-a-title"
    assert(titleParser(fn) === None)

    val fn2 = "this-is-a-title"
    assert(titleParser(fn2) === None)

    val fn3 = "2022-08-24"
    assert(titleParser(fn3) === None)
  }

class PurifyUrlSpecs extends AnyFlatSpec:

  import StringProcessors.purifyUrl

  it should "remove multiple backslashes properly" in {
    val f = "/url//with////lots///s"
    val t = "/url/with/lots/s"
    assert(purifyUrl(f) === t)
  }

  it should "encode url properly" in {
    val f = "//url/ with spacÈ"
    val t = "/url/%20with%20spac%C3%88"
    assert(purifyUrl(f) === t)
  }
