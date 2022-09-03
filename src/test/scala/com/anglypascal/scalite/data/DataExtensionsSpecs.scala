package com.anglypascal.scalite.data

import com.anglypascal.scalite.data.DataExtensions.*

import org.scalatest.flatspec.AnyFlatSpec
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}

class DataExtensionsSpecs extends AnyFlatSpec:
  val m1 = MObj("a" -> "1", "b" -> "1")
  val m2 = MObj("a" -> "2", "c" -> "2")
  val m3 = MObj("a" -> "3", "d" -> "3")

  val i1 = IObj("a" -> "1", "e" -> "1")
  val i2 = IObj("a" -> "2", "f" -> "2")
  val i3 = IObj("a" -> "3", "g" -> "3")
  
  it should "chain get or else properly" in {
    assert(getChain(m1, m2, i1)("a")("1") === "1")
    assert(getChain(m1, m2, i1)("a")("1") === "1")

  }
