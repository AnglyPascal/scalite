package com.anglypascal.scalite

import com.anglypascal.scalite.utils.StringProcessors.quote
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.groups.PostCluster
import com.anglypascal.scalite.collections.PageLike
import com.anglypascal.scalite.collections.Excerpt

@main
def main =

  // val str = """
// [1] hello
// [2] world
// some text with [a][2]

// [3] bye bye
  // """

  // val regex = """ {0,3}(?:(\[[^\]]+\])(.+))""".r

  // for m <- regex.findAllMatchIn(str) do 
  //   println(m.toString())

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

  val exc = Excerpt(post, IObj(), "<-->")
  println(exc.content)

  Converters.reset()
  Layouts.reset()
