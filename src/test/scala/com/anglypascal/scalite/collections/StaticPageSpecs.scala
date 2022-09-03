package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.layouts.MustacheLayouts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Markdown
import com.anglypascal.scalite.utils.DirectoryReader
import java.nio.file.Files
import java.nio.file.Paths

class StaticPageSpecs extends AnyFlatSpec:

  val pD = "src/test/resources/site_template/_statics"
  val rP = "/index.md"
  val glb1 = DObj()
  val clcs = DObj()

  ignore should "handle rendering and file creation properly" in {
    DirectoryReader("src/test/resources/site_template/_site")
    Layouts(
      com.anglypascal.scalite.data.mutable.DObj(),
      DObj()
    )
    val pst = new PageLike("statics")(pD, rP, glb1, clcs)
    pst.write()
    val p = Paths.get("src/test/resources/site_template/_site/index.html")
    assert(Files.exists(p))
    Files.delete(p)
    assert(!Files.exists(p))
  }
