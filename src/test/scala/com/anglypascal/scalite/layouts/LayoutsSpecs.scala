package com.anglypascal.scalite.layouts

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.utils.DirectoryReader
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}

class LayoutsSpecs extends AnyFlatSpec:
  
  val pDir = "src/test/resources/site_template"
  val globals = IObj("base" -> pDir)

  DirectoryReader(pDir + "/_site")
  Layouts(MObj(), globals)

  it should "load all layouts properly" in {
    println(Layouts)
  }
