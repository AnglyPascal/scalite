package com.anglypascal.scalite

import com.anglypascal.scalite.collections.Collections
import org.scalatest.flatspec.AnyFlatSpec

class GlobalsSpec extends AnyFlatSpec:
  val root = "src/test/resources/site_template"
  val globals = Globals(root)
  
  println(globals)
  println(Collections)
  // globals.print()

