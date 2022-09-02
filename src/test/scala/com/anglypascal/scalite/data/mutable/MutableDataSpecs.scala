package com.anglypascal.scalite.data.mutable

import org.scalatest.flatspec.AnyFlatSpec
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Str

import scala.collection.mutable.Map

class MutableDataSpecs extends AnyFlatSpec:

  it should "handle basic object creation" in {
    val s1 = DStr("hello")
    val n1 = DNum(1)
    val b1 = DBool(true)
    val a1 = DArr(s1, n1, b1)

    assert(
      s1.str === "hello" &&
        n1.num === 1 &&
        b1.bool &&
        a1(0) === s1 &&
        a1(1) === n1 &&
        a1(2) === b1
    )
  }

  it should "handle DStr properly" in {
    val s1 = DStr("hello,")
    val s2 = " world"
    val s3 = DStr("!")
    assert((s1 + s2 + s3).str === "hello, world!")

    val t1 = DStr("abc")
    val t2 = DStr("def")
    assert(t1 < t2)
  }

  it should "handle DArr creation properly" in {
    val s1 = DStr("hello")
    val n1 = DNum(1)
    val b1 = DBool(true)
    val o1 = Arr("hello", 1, true)

    val a1 = DArr(s1, n1, b1)
    val a2 = DArr(o1)

    assert(a1(0) === a2(0))
  }

  it should "handle DObj properly" in {
    val o1 = DObj(
      "a" -> DStr("1"),
      "b" -> DNum(2),
      "c" -> DBool(true),
      "d" -> DArr(DStr("a"), DStr("b"), DStr("c")),
      "e" -> DObj(
        "1" -> DNum(1),
        "2" -> DNum(2)
      )
    )

    assert(
      o1("a") === DStr("1") &&
        o1("b") === DNum(2) &&
        o1("c").getBool.get
    )

    assert(
      o1.getOrElse("a")(2) === 2 &&
        o1.getOrElse("a")("b") === "1" &&
        o1.getOrElse("a")(true)
    )

    assert(
      o1.getOrElse("e")(DObj()).getOrElse("1")(0) === 1 &&
        o1.getOrElse("e")(DObj()).getOrElse("2")(0) === 2 &&
        o1.getOrElse("e")(DObj()).getOrElse("3")(2) === 2 &&
        o1.getOrElse("e")(Map[String, Data]())("1") === DNum(1) &&
        o1.getOrElse("e")(Map[String, Data]())("2") === DNum(2) &&
        o1.getOrElse("e")(Map[String, Data]()).get("3") === None
    )

    assert(
      o1.getOrElse("d")(DArr())(0) === DStr("a") &&
        o1.getOrElse("d")(DArr())(1) === DStr("b")
    )

  }
