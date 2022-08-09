package com.anglypascal.scalite.utils

import com.rallyhealth.weejson.v1.{Value, Obj, Arr, Str, Num, Bool, Null}
import scala.language.implicitConversions
import com.anglypascal.mustache.AST
import scala.collection.mutable

/** TODO: This might become performance heavy :( idk, but let's keep going. Also
  * is this a good api? I'm extending AST, which the other user shouldn't have
  * to :/
  */
sealed trait Data extends AST

/** Wrapper for Obj and mutable maps for use in the context */
class DObj(private val _obj: mutable.Map[String, Data]) extends Data:

  def contains(key: String) = _obj.contains(key: String)
  def apply(key: String): Data = _obj(key)
  def get(key: String): Option[Data] = _obj.get(key)

  def content = _obj("content")
  def content_=(c: String) = _obj("content") = DStr(c)

  def findKey(key: String): Option[Any] = get(key)
  def value: Any = _obj

object DObj:
  def apply(_obj: mutable.Map[String, Data]) = new DObj(_obj)
  def apply(_obj: Obj) =
    new DObj(_obj.obj.map((k, v) => (k, DataImplicits.valueToData(v))))

/** Wrapper for Arr and mutable ArrayBuffer for use in the context */
class DArr(private val _arr: List[Data]) extends Data:

  def apply(ind: Int): Data = _arr(ind)
  def head: Data = _arr.head
  def tail: DArr = DArr(_arr.tail)

  def findKey(key: String): Option[Any] = Some(value)
  def value: Any = _arr

object DArr:
  def apply(_arr: List[Data]) = new DArr(_arr)
  def apply(_arr: Arr) =
    new DArr(_arr.arr.map(DataImplicits.valueToData).toList)

/** Wrapper for string */
class DStr(private val _str: String) extends Data:
  def str = _str

  def findKey(key: String): Option[Any] = Some(value)
  def value: Any = _str

object DStr:
  def apply(_str: String) = new DStr(_str)
  def apply(_str: Str) = new DStr(_str.str)

/** Wrapper for numbers */
class DNum(private val _num: BigDecimal) extends Data:
  def num = _num

  def findKey(key: String): Option[Any] = Some(value)
  def value: Any = _num

object DNum:
  def apply(_num: Double) = new DNum(_num)
  def apply(_num: BigDecimal) = new DNum(_num)
  def apply(_num: Num) = new DNum(_num.num)

/** Wrapper for boolean values */
class DBool(private val _bool: Boolean) extends Data:
  def bool = _bool

  def findKey(key: String): Option[Any] = Some(value)
  def value: Any = _bool

object DBool:
  def apply(_bool: Boolean) = new DBool(_bool)
  def apply(_bool: Bool) = new DBool(_bool.bool)

/** Lol null */
object DNull extends Data:
  def findKey(key: String): Option[Any] = None
  def value: Any = None

/** Implicit conversion methods for converting Value to Data and from Data to
  * primitives
  */
object DataImplicits:

  implicit def dstrToString(dstr: DStr): String = dstr.str
  implicit def dnumToBigDecimal(dstr: DNum): BigDecimal = dstr.num
  implicit def dboolToBoolean(dbool: DBool): Boolean = dbool.bool

  implicit def valueToData(v: Value): Data =
    v match
      case v: Obj  => DObj(v)
      case v: Arr  => DArr(v)
      case v: Str  => DStr(v)
      case v: Num  => DNum(v)
      case v: Bool => DBool(v)
      case Null    => DNull
