package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.utils.DirectoryReader
import com.anglypascal.scalite.groups.Groups

class PostsSpecs extends AnyFlatSpec:
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
    IObj()
  )

  it should "read all posts properly" in {
    println(Posts)
  }

  /** Check the overriding mechanism of configs */
