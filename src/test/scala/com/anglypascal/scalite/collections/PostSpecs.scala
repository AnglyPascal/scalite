package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Markdown
import com.anglypascal.scalite.data.immutable.DStr
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.layouts.MustacheLayouts
import com.anglypascal.scalite.utils.DirectoryReader
import org.scalatest.flatspec.AnyFlatSpec

import java.nio.file.Files
import java.nio.file.Paths
import com.anglypascal.scalite.trees.Forests

class PostSpecs extends AnyFlatSpec:

  val pDir = "src/test/resources/site_template/_posts"
  val rPth = "/2016-05-19-super-short-article.md"
  val glb1 = IObj(
    "title" -> "Test1",
    "rootUrl" -> "hello.world.com",
    "base" -> "src/test/resources/site_template"
  )
  val glb2 = IObj(
    "dateFormat" -> DStr("dd MMM, yyyy")
  )
  val clcs = IObj()

  it should "read valid file properly" in {
    Collections.reset()
    Converters.reset()
    Layouts.reset()
    Forests.reset()

    val pst = new PostLike("posts")(pDir, rPth, glb1, clcs)
    assert(
      pst.title === "Super Short Article" &&
        pst.date === "2016-05-19" &&
        pst.locals("date").getStr.getOrElse("") === "2016-05-19" &&
        pst.visible
    )

    val pst1 = new PostLike("posts")(pDir, rPth, glb2, clcs)
    assert(
      pst1.title === "Super Short Article" &&
        pst1.date === "19 May, 2016" &&
        pst1.locals("date").getStr.getOrElse("") === "19 May, 2016"
    )
  }

  it should "read files with groups properly" in {
    val rPth1 = "/2022-08-29-categories-test.md"
    val pst = new PostLike("posts")(pDir, rPth1, glb1, clcs)
    assert(
      pst.title === "A page with categories" &&
        pst.date === "2022-08-29" &&
        pst.locals("date").getStr.getOrElse("") === "2022-08-29" &&
        pst.visible
    )
  }

  it should "handle rendering and file creation properly" in {
    Collections.reset()
    Converters.reset()
    Layouts.reset()
    Forests.reset()

    DirectoryReader("src/test/resources/site_template/_site")
    Layouts(MObj(), glb1)

    val pst = new PostLike("posts")(pDir, rPth, glb1, clcs)
    pst.write(false)
    val p = Paths.get(
      "src/test/resources/site_template/_site" +
        "/posts/2016/05/19/super-short-article.html"
    )
    assert(Files.exists(p))
    Files.delete(p)
    assert(!Files.exists(p))

    Layouts.reset()
  }

  it should "handle excerpts properly" in {
    Collections.reset()
    Converters.reset()
    Layouts.reset()
    Forests.reset()

    val pDir = "src/test/resources/site_template/_posts"
    val pPth = "/2022-09-12-post-with-links.md"
    Converters(MObj(), glb1)
    Layouts(MObj(), glb1)

    val post = PostLike("posts")(pDir, pPth, glb1, clcs)
    val locals = post.locals

    val ex = locals.getOrElse("excerpt")("")

    assert(
      ex.contains("""<a href="this_is_another_link">b</a>""") &&
        ex.contains("second paragraph")
    )

    Converters.reset()
    Layouts.reset()
  }
