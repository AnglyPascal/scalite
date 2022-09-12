package com.anglypascal.scalite.converters

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.immutable.DObj

class SassConverterSpecs extends AnyFlatSpec:

  val globals = DObj(
    "base" -> "src/test/resources/site_template",
    "sassDir" -> "/_sass"
  )
  val sass = new Sass(DObj(), globals)

  it should "compile simple strings" in {
    val css = sass.convert("foo { color: #000000 }")
    assert(css === "foo{color:#000}")
  }

  it should "compile simple imports" in {
    val css = sass.convert("@import 'bar'")
    assert(css === "html{color:red}")

    val css2 = sass.convert("@import 'baz'")
    assert(css2 === "html{color:green}")
  }
