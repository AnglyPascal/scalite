package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.layouts.Layouts
import org.scalatest.flatspec.AnyFlatSpec

class ExcerptSpecs extends AnyFlatSpec:

  Converters.reset()
  Layouts.reset()

  val pDir = "src/test/resources/site_template"
  val pPth = "/_posts"
  val globals = IObj("base" -> pDir)
  Converters(MObj(), globals)
  Layouts(MObj(), globals)

  val filepath =
    "src/test/resources/site_template/_posts/2022-09-12-post-with-links.md"
  val mainMatter = """
Hello, this is the first paragraph, with some links: [a][1], [b][2]

This is the second paragraph.

<-->

This is the last paragraph, which shouldn't be in the excerpt.

[1]: www.google.com
[2]: this_is_another_link 
  """

  it should "get the excerpt with default separator properly" in {
    val exc = Excerpt(mainMatter, filepath, true, "\n\n")(IObj(), IObj())
    assert(
      exc.content.contains("""<a href="this_is_another_link">b</a>""") &&
        !exc.content.contains("second paragraph")
    )
  }

  it should "get the excerpt with custom separator properly" in {
    val exc = Excerpt(mainMatter, filepath, true, "<-->")(IObj(), IObj())
    assert(
      exc.content.contains("""<a href="this_is_another_link">b</a>""") &&
        exc.content.contains("second paragraph")
    )
  }
