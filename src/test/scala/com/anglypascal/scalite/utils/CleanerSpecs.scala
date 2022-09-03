package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Arr
import com.anglypascal.scalite.data.immutable.DObj

class CleanerSpecs extends AnyFlatSpec:
  val ob1 = Obj(
    "base" -> "src/test/resources/site_template",
    "destination" -> "/_site",
    "keepFiles" -> Arr()
  )
  val ob2 = Obj(
    "base" -> "src/test/resources/site_template",
    "destination" -> "/_site",
    "keepFiles" -> Arr(".*\\.jpg")
  )

  ignore should "clean target diectory properly" in {
    Cleaner(DObj(ob1))
  }
