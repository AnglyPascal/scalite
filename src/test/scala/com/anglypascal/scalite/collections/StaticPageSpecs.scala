package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
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

  it should "handle rendering and file creation properly" in {
    Converters.addConverter(Markdown)
    Layouts.addEngine(MustacheLayouts)
    DirectoryReader("src/test/resources/site_template/_site")
    Layouts(
      "src/test/resources/site_template/_layouts",
      "src/test/resources/site_template/_partials"
    )
    val pst = new StaticPage(pD, rP, glb1, clcs)
    pst.write()
    val p = Paths.get("src/test/resources/site_template/_site/index.html")
    assert(Files.exists(p))
    Files.delete(p)
    assert(!Files.exists(p))
  }
