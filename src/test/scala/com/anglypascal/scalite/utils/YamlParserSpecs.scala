package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec

class YamlParserSpecs extends AnyFlatSpec:

  it should "parser yaml properly" in {
    val yaml = "hello: world"
    val obj = yamlParser(yaml)
    assert(obj("hello").str === "world")
  }
  
