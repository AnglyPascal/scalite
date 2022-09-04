package com.anglypascal.scalite.layouts

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.utils.DirectoryReader
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.Future
import com.anglypascal.scalite.utils.Colors.*

class LayoutsSpecs extends AsyncFlatSpec:

  val pDir = "src/test/resources/site_template"
  val globals = IObj("base" -> pDir)

  DirectoryReader(pDir + "/_site")
  Layouts(MObj(), globals)

  it should "load all layouts properly" in {
    val future = Future {
      Layouts
    }

    future.map(l =>
      val d = l.get("default")
      val m = l.get("main")
      assert(
        d != None && d.get.name == "default" &&
          m != None && m.get.name == "main"
      )
    )
  }
