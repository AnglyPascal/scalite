package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr

class CleanerSpecs extends AnyFlatSpec:
  val obj1 = MObj(
    "keepFiles" -> DArr()
  )
  val obj2 = IObj(
    "base" -> "src/test/resources/site_template",
    "destination" -> "/_site",
  )

  ignore should "clean target diectory properly" in {
    Cleaner(obj1, obj2)
  }
