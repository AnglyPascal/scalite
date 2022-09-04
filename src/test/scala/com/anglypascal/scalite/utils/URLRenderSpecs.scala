package com.anglypascal.scalite.utils

import com.anglypascal.scalite.URL
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.utils.DateParser.dateParseObj
import com.rallyhealth.weejson.v1.Obj
import org.scalatest.flatspec.AnyFlatSpec

class URLRenderSpecs extends AnyFlatSpec:
  val time = "2022-08-28 18:15:30"
  val date = dateParseObj(time, "yyyy-MM-dd HH-mm-ss z")
  date += "title" -> "test_title"
  date += "slugTitle" -> "test-title"
  date += "categories" -> "cat1/cat2"
  val dobj = DObj(date)

  it should "render urls properly" in {
    assert(URL("/{{>pretty}}")(dobj) === "/cat1/cat2/2022/08/28/test_title")
    assert(URL("/{{year}}/{{>none}}")(dobj) === "/2022/cat1/cat2/test-title")
  }

