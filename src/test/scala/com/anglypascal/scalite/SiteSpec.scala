package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.trees.PostForests
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.utils.Colors.*
import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AsyncFlatSpec

import scala.concurrent.Future
import com.anglypascal.scalite.trees.Forests

// @DoNotDiscover
class SiteSpecs extends AsyncFlatSpec:
  val root = "src/test/resources/site_template"
  val globals = Site(root).globals

  it should "read the configs properly" in {
    val future = Future { globals }

    future.map(glbs => assert(!globals.contains("collections")))
  }

  "Converters" should "process all converters properly" in {
    val future = Future {
      println(ERROR("\nCONVERTERS"))
      println(Converters)
    }

    future.map(unit => assert(true))
  }

  "Layouts" should "process the layouts properly" in {
    val future = Future {
      println(ERROR("\nLAYOUTS"))
      println(Layouts)
    }

    future.map(unit => assert(true))
  }

  "Collections" should "process the collections properly" in {
    val future = Future {
      println(ERROR("\nCOLLECTIONS"))
      println(Collections)
    }

    future.map(unit => assert(true))
  }

  "Trees" should "process the trees properly" in {
    val future = Future {
      println(ERROR("\nTREES"))
      println(Forests)
    }
    future.map(unit => assert(true))
  }
