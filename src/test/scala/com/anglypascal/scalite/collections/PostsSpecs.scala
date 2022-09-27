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

class PostsSpecs extends AsyncFlatSpec:

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

  it should "read all posts properly" in {
    val posts =
      Collection(PostConstructor, "posts", pDir + pPth, configs, globals)
    val future = Future { posts }

    future.map(l =>
      println(posts)
      assert(l.items.toList.length === 7)
    )
  }

  it should "sort and filter the posts properly" in {
    val posts =
      Collection(PostConstructor, "posts", pDir + pPth, configs, globals)
    val future = Future { posts }

    future.map(l =>
      assert(l.sortedItems.length === 6)
    )
  }

  it should "create local variables properly" in {
    val posts =
      Collection(PostConstructor, "posts", pDir + pPth, configs, globals)
    val future = Future { posts }

    future.map(l =>
      println(l.locals)
      assert(
        l.locals.getOrElse("haha")("") === "bruh"
          && !l.locals.getOrElse("toc")(false)
      )
    )
  }

  /** Check the overriding mechanism of configs */
