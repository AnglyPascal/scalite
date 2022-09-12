package com.anglypascal.scalite.collections

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.immutable.DObj

class ExcerptSpecs extends AnyFlatSpec:

  val post = PostLike("post")(
    "src/test/resources/site_template/_posts",
    "/2022-09-12-post-with-links.md",
    DObj(),
    DObj()
  )

  it should "get the excerpt before custom properly" in {
    val exc = Excerpt(post, DObj(), "<-->")
    println(exc.content)
  }
