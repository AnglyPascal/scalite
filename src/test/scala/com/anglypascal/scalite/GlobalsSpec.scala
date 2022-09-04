package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.layouts.Layouts
import org.scalatest.DoNotDiscover

// @DoNotDiscover
class GlobalsSpec extends AnyFlatSpec:
  val root = "src/test/resources/site_template"
  val globals = Globals(root)
  
  it should "read the configs properly" in {
    println(globals)
  }

  it should "read the collections properly" in {
    println(Collections)
  }

  it should "read the layouts properly" in {
    println(Layouts)
  }
