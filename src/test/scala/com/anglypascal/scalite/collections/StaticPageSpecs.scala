package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.utils.DirectoryReader
import java.nio.file.Files
import java.nio.file.Paths

class StaticPageSpecs extends AnyFlatSpec:

  val pD = "src/test/resources/site_template/_statics"
  val rP = "/index.md"
  val globals = IObj("base" -> "src/test/resources/site_template")
  val clcs = IObj()

  it should "handle rendering and file creation properly" in {
    DirectoryReader("src/test/resources/site_template/_site")
    Layouts(MObj(), globals)
    val pst = new PageLike("statics")(pD, rP, globals, clcs)
    pst.write()
    val p = Paths.get("src/test/resources/site_template/_site/index.html")
    assert(Files.exists(p))
    Files.delete(p)
    assert(!Files.exists(p))
  }
