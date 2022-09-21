package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.trees.Forests
import com.anglypascal.scalite.trees.PostForests
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DirectoryReader
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.Future

class CollectionsSpecs extends AsyncFlatSpec:

  Converters.reset()
  Layouts.reset()
  Forests.reset()

  val pDir = "src/test/resources/site_template"
  val pPth = "/_posts"
  val configs = MObj(
    "toc" -> true,
    "sortBy" -> "date, title",
    "outputExt" -> ".html",
    "haha" -> "bruh"
  )
  val globals = IObj("base" -> pDir)

  DirectoryReader(pDir + "/_site")
  Layouts(MObj(), globals)
  Converters(MObj(), globals)
  PostForests(MObj(), globals)

  val posts =
    Collection(PostConstructor, "posts", pDir + pPth, configs, globals)

