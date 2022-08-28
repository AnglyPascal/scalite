package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.layouts.MustacheLayouts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Markdown

class PostSpecs extends AnyFlatSpec:

  val pDir = "src/test/resources/site_template"
  val rPth = "/_posts/2016-05-19-super-short-article.md"
  val rPthNon = "/_posts/post-that-never-was.md"
  val rPthInv = "/_posts/2016-05-post-without-valid-date.md"
  val glb1 = DObj()
  val glb2 = DObj(
    "dateFormat" -> DStr("dd MMM, yyyy")
  )
  val clcs = DObj()

  ignore should "read valid file properly" in {
    val pst = new Post(pDir, rPth, glb1, clcs)
    assert(
      pst.title === "Super Short Article" &&
        pst.date === "2016-05-19" &&
        pst.locals("date").getStr.getOrElse("") === "2016-05-19" &&
        pst.visible
    )

    val pst1 = new Post(pDir, rPth, glb2, clcs)
    assert(
      pst1.title === "Super Short Article" &&
        pst1.date === "19 May, 2016" &&
        pst1.locals("date").getStr.getOrElse("") === "19 May, 2016"
    )
  }

  it should "handle layout rendering properly" in {
    Converters.addConverter(Markdown)
    Layouts.addEngine(MustacheLayouts)
    Layouts(
      "src/test/resources/site_template/_layouts",
      "src/test/resources/site_template/_partials"
    )
    val pst = new Post(pDir, rPth, glb1, clcs)
    pst.write(true)
  }
