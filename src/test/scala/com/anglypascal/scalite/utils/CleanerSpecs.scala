package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr

class CleanerSpecs extends AnyFlatSpec:
  val ob1 = MObj(
    "base" -> "src/test/resources/site_template",
    "destination" -> "/_site",
    "keepFiles" -> DArr()
  )
  val ob2 = MObj(
    "base" -> "src/test/resources/site_template",
    "destination" -> "/_site",
    "keepFiles" -> DArr(".*\\.jpg")
  )

  ignore should "clean target diectory properly" in {
    Cleaner(IObj(ob1))
  }
