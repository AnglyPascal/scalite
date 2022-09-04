package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.groups.Groups
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DirectoryReader
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.Future

class PostsSpecs extends AsyncFlatSpec:
  val pDir = "src/test/resources/site_template"
  val pPth = "/_posts"
  val globals = IObj("base" -> pDir)

  DirectoryReader(pDir + "/_site")
  Layouts(MObj(), globals)
  Converters(MObj(), globals)
  Groups(MObj(), globals)

  val Posts = Collection(PostConstructor, "posts", "post")(
    pDir + pPth,
    globals,
    "",
    false,
    "",
    MObj()
  )

  it should "read all posts properly" in {
    val future = Future { Posts }

    future.map(l => assert(l.items.toList.length === 3))
  }

  /** Check the overriding mechanism of configs */
