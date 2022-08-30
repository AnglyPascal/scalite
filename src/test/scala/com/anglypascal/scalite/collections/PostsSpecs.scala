package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.layouts.MustacheLayouts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Markdown
import com.anglypascal.scalite.utils.DirectoryReader

class PostsSpecs extends AnyFlatSpec:
  val pDir = "src/test/resources/site_template"
  val pPth = "/_posts"
  val globals = DObj()

  Converters.addConverter(Markdown)
  Layouts.addEngine(MustacheLayouts)
  DirectoryReader(pDir + "/_site")
  Layouts(pDir + "/_layouts", pDir + "/_partials")

  val Posts = Collection(postConstructor, "posts", "post")

  ignore should "read all posts properly" in {
    Posts.setup(pDir + pPth, globals)
    Posts.process(true)
  }

  /** Check the overriding mechanism of configs */
