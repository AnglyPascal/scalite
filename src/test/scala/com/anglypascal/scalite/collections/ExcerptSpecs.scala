package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.layouts.Layouts

class ExcerptSpecs extends AnyFlatSpec:

  Converters.reset()
  Layouts.reset()

  val pDir = "src/test/resources/site_template"
  val pPth = "/_posts"
  val globals = IObj("base" -> pDir)
  Converters(MObj(), globals)
  Layouts(MObj(), globals)

  val post = PageLike("page")(
    "src/test/resources/site_template/_posts",
    "/2022-09-12-post-with-links.md",
    IObj(),
    IObj()
  )

  it should "get the excerpt before custom properly" in {
    val exc = Excerpt(post, IObj(), "<-->")
    assert(exc.content.contains("""<a href="this_is_another_link">b</a>"""))
  }
