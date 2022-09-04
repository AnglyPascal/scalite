package com.anglypascal.scalite.utils

import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DBool
import com.anglypascal.scalite.data.mutable.DNum
import com.anglypascal.scalite.data.mutable.DObj
import com.anglypascal.scalite.data.mutable.DStr
import org.scalatest.flatspec.AnyFlatSpec

class YamlParserSpecs extends AnyFlatSpec:

  it should "parse yaml string properly" in {
    val yaml = "hello: world"
    val obj = frontMatterParser(yaml)
    assert(obj("hello") === DStr("world"))
  }

  val path = "src/test/resources/test.yml"
  val yaml = yamlFileParser(path) match
    case o: DObj => o
    case _       => DObj()

  it should "parse yaml file properly" in {
    val obj = yaml.getOrElse("object")(DObj())
    val subObj = obj.getOrElse("array")(DArr())(4).asInstanceOf[DObj]
    assert(
      obj.getOrElse("string")("1") === "hello, world!" &&
        obj.getOrElse("number")(0) === 1 &&
        obj.getOrElse("boolean")(false) &&
        obj.getOrElse("array")(DArr())(0) === DStr("1") &&
        obj.getOrElse("array")(DArr())(1) === DNum(2) &&
        obj.getOrElse("array")(DArr())(2) === DStr("item") &&
        obj.getOrElse("array")(DArr())(3).getArr.get(0) === DNum(1) &&
        obj.getOrElse("array")(DArr())(3).getArr.get(0) === DNum(1) &&
        subObj.getOrElse("subObj")(DObj()).getOrElse("b")(1) === 2
    )
  }

  it should "parse arrays as expected" in {
    val obj = yaml.getOrElse("another")(DObj())
    val nums = obj.getOrElse("nums")(DArr())
    val strs = obj.getOrElse("strs")(DArr())
    val arr = obj.getOrElse("arr")(DArr())

    assert(
      nums(0) === DNum(1) &&
        strs(1) === DStr("2") &&
        arr(0) === DStr("1") &&
        arr(1) === DNum(2) &&
        arr(2) === DBool(false)
    )
  }
