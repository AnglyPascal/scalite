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
import com.anglypascal.scalite.groups.*

class PostSpecs extends AnyFlatSpec:

  val pDir = "src/test/resources/site_template/_posts"
  val rPth = "/2016-05-19-super-short-article.md"
  val glb1 = DObj()
  val glb2 = DObj(
    "dateFormat" -> DStr("dd MMM, yyyy")
  )
  val clcs = DObj()

  it should "read valid file properly" in {
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

  it should "read files with groups properly" in {
    Groups.addNewGroup(Tags)
    Groups.addNewGroup(Categories)
    val rPth1 = "/2022-08-29-categories-test.md"
    val pst = new Post(pDir, rPth1, glb1, clcs)
    assert(
      pst.title === "A page with categories" &&
        pst.date === "2022-08-29" &&
        pst.locals("date").getStr.getOrElse("") === "2022-08-29" &&
        pst.visible
    )
  }

  ignore should "handle rendering and file creation properly" in {
    Converters.addConverter(Markdown)
    Layouts.addEngine(MustacheLayouts)
    DirectoryReader("src/test/resources/site_template/_site")
    Layouts(
      "src/test/resources/site_template/_layouts",
      "src/test/resources/site_template/_partials"
    )
    val pst = new Post(pDir, rPth, glb1, clcs)
    pst.write()
    val p = Paths.get(
      "src/test/resources/site_template" +
        "/_site/posts/2016/05/19/super-short-article.html"
    )
    assert(Files.exists(p))
    Files.delete(p)
    assert(!Files.exists(p))
  }
