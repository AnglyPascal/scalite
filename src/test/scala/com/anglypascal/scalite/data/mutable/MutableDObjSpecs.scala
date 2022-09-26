package com.anglypascal.scalite.data.mutable

import org.scalatest.flatspec.AnyFlatSpec
import com.rallyhealth.weejson.v1.Obj

class MutableDObjSpecs extends AnyFlatSpec:

  it should "handle object creation with sugar syntax" in {
    val obj = DObj(
      "hello" -> "world",
      "arr" -> DArr(1, "2", 3, DArr("1", "2")),
      "obj" -> DObj(
        "1" -> 1,
        "2" -> "2"
      )
    )
    assert(
      obj("hello") == DStr("world") &&
        obj.getOrElse("obj")(DObj()).getOrElse("1")(2) === 1 &&
        obj.getOrElse("obj")(DObj()).getOrElse("2")(3) === 3 &&
        obj.getOrElse("arr")(DArr())(2) === DNum(3)
    )
  }

  it should "handle incremental DObj creation" in {
    val dobj = DObj()
    val N = 1000000
    for i <- 0 until N do dobj.addOne(i.toString -> DNum(i))
    assert(dobj.toList.length === N)
    for i <- 0 until N do dobj.addOne("1" -> DNum(i))
    assert(dobj.toList.length === N && dobj.getOrElse("1")(1) === N - 1)
  }

  it should "handle get(key) calls to DObj properly" in {
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
        o1.getOrElse("e")(DObj()).getOrElse("3")(2) === 2
    )

    assert(
      o1.getOrElse("d")(DArr())(0) === DStr("a") &&
        o1.getOrElse("d")(DArr())(1) === DStr("b")
    )
  }

  it should "handle update calls to DObj properly" in {
    val o1 = DObj(
      "a" -> DStr("1"),
      "b" -> DNum(2),
      "c" -> DBool(true)
    )
    o1("a") = DBool(false)
    o1("b") = DStr("1")
    o1("c") = DNum(1)

    assert(
      !o1("a").getBool.get &&
        o1("b") === DStr("1") &&
        o1("c") === DNum(1)
    )
  }

  it should "handle update by another DObj" in {
    val obj = DObj(
      "hello" -> "world",
      "number" -> 1,
      "bool" -> true,
      "arr" -> DArr(1, "2", 3, DArr("1", "2")),
      "obj" -> DObj(
        "1" -> 1,
        "2" -> "2"
      )
    )

    val obj2 = DObj(
      "hello" -> "goodbye",
      "obj" -> DObj(
        "1" -> "3",
        "2" -> "3"
      ),
      "new" -> "word"
    )

    val o1 = obj update obj2

    assert(
      obj.getOrElse("obj")(DObj()).getOrElse("2")("1") === "3" &&
        obj.getOrElse("obj")(DObj()).getOrElse("1")("3") === "3" &&
        obj.getOrElse("new")("new") === "word" &&
        obj.getOrElse("hello")("world") === "goodbye"
    )

    assert(
      o1.getOrElse("obj")(DObj()).getOrElse("2")("1") === "3" &&
        o1.getOrElse("obj")(DObj()).getOrElse("1")("3") === "3" &&
        o1.getOrElse("new")("new") === "word" &&
        o1.getOrElse("hello")("world") === "goodbye"
    )

    assert(
      obj2.getOrElse("obj")(DObj()).getOrElse("2")("1") === "3" &&
        obj2.getOrElse("obj")(DObj()).getOrElse("1")(1) === 1 &&
        obj2.getOrElse("hello")("world") === "goodbye"
    )
  }

  it should "handle updates by WeeJson Obj properly" in {
    val o1 = DObj(
      "a" -> DStr("1"),
      "b" -> DNum(2),
      "c" -> DBool(true)
    )
    val o2 = Obj(
      "a" -> "2",
      "b" -> 1,
      "c" -> false
    )
    o1.update(o2)

    assert(
      !o1("c").getBool.get &&
        o1("a") === DStr("2") &&
        o1("b") === DNum(1)
    )
  }

  it should "handle map calls properly" in {
    val obj = DObj(
      "1" -> "10",
      "2" -> "20",
      "3" -> 30,
      "4" -> DArr(40)
    )

    val l = obj.map(_._2.getStr).toArray

    assert(
      obj.getOrElse("1")("") === "10" &&
        obj.getOrElse("2")("") === "20" &&
        obj.getOrElse("3")(1) === 30 &&
        obj.getOrElse("4")(DArr())(0) === DNum(40)
    )
    assert(
      l(0) === Some("10") && l(1) === Some("20")
        && l(2) === None && l(3) === None
    )

    val n = obj.map(_._2.getNum).toArray

    assert(
      obj.getOrElse("1")("") === "10" &&
        obj.getOrElse("2")("") === "20" &&
        obj.getOrElse("3")(1) === 30 &&
        obj.getOrElse("4")(DArr())(0) === DNum(40)
    )
    assert(
      n(2) === Some(30) && n(1) === None
    )
  }
