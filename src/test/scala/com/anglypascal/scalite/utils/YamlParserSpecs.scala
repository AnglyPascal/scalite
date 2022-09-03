package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.mutable.DStr

class YamlParserSpecs extends AnyFlatSpec:

  it should "parser yaml properly" in {
    val yaml = "hello: world"
    val obj = frontMatterParser(yaml)
    assert(obj("hello") === DStr("world"))
  }
  
