package com.anglypascal.scalite.layouts

import helpers.SlugHelper
import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.mustache.Mustache

class MustacheLayoutHelpersSpecs extends AnyFlatSpec:

  class M(template: String) extends Mustache(template) with SlugHelper

  val temp = "{{#slug}}{{title}}{{/slug}}"
  val m = Map(
    "title" -> "hello world"
  )

  it should "render slug helper properly" in {
    val mus = M(temp)
    assert(mus.render(m) === "hello-world")
  }
