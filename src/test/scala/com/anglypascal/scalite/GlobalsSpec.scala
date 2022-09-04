package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.utils.Colors.*
import org.scalatest.DoNotDiscover
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.groups.Groups

// @DoNotDiscover
class GlobalsSpec extends AnyFlatSpec:
  val root = "src/test/resources/site_template"
  val globals = Globals(root)
  
  it should "read the configs properly" in {
    println(ERROR("\nCONVERTERS"))
    println(Converters)
    println(ERROR("\nCOLLECTIONS"))
    println(Collections)
    println(ERROR("\nGROUPS"))
    println(Groups)
    println(ERROR("\nLAYOUTS"))
    println(Layouts)
  }
