package com.anglypascal.scalite.utils

import com.rallyhealth.weejson.v1.{Value, Obj, Arr, Str, Num, Bool, Null}
import scala.language.implicitConversions
import scala.collection.mutable

/** Immutable wrapper around WeeJson Value AST */
trait Data

/** Immutable wrapper around Obj. Provides only one mutable entry for content
  * for performance reasons.
  */
final class DObj(private val _obj: mutable.Map[String, Data]) extends Data:

  /** Returns the underlying mutable.Map[String, Data] */
  def obj = _obj

  /** Returns a new DObj with the given mutable Map */
  def obj_=(o: mutable.Map[String, Data]) = DObj(o)

  /** Returns a new DObj with the given Obj */
  def obj_=(o: Obj) = DObj(o)

  /** Returns the value stored against key in the underlying Map */
  def contains(key: String) = _obj.contains(key: String)

  /** Returns the value stored against key in the underlying Map */
  def apply(key: String): Data = _obj(key)

  /** Returns the value stored against key in the underlying Map wrapped in
    * Option
    */
  def get(key: String): Option[Data] = _obj.get(key)

  /** Return a new DObj object with the given pair added */
  def add(pair: (String, Data)): DObj =
    val _nobj = _obj.clone()
    _nobj += pair
    DObj(_nobj)

  /** Returns the value stored against the key "content" */
  def content = _obj("content")

  /** Update the value stored against the key "content" */
  def content_=(c: String) = _obj("content") = DStr(c)

  override def toString(): String = _obj.toString

/** Companion object to provide factory constructors. */
object DObj:
  /** Construct a DObj from a mutable Map */
  def apply(_obj: mutable.Map[String, Data]) = new DObj(_obj)

  def apply(pairs: Tuple2[String, Data]*) = new DObj(
    mutable.Map(pairs: _*)
  )

  /** Construct a DObj from an Obj */
  def apply(_obj: Obj) =
    new DObj(_obj.obj.map((k, v) => (k, DataImplicits.valueToData(v))))

/** Immutable wrapper around Arr */
final class DArr(private val _arr: List[Data]) extends Data:

  /** Get the underlying List[Data] */
  def arr = _arr

  /** Return a new DArr with the new List[Data] */
  def arr_=(a: List[Data]) = DArr(a)

  /** Return the ind entry of the List[Data], inefficient TODO */
  def apply(ind: Int): Data = _arr(ind)

  /** Add a data entry to the front of the list, returning a new DArr */
  def add(entry: Data): DArr = DArr(entry :: _arr)

  def head: Data = _arr.head
  def tail: DArr = DArr(_arr.tail)

  override def toString(): String = _arr.mkString(", ")

/** Companion object to DArr to provide factory constructors */
object DArr:

  /** Create a new DArr from a List[Data] */
  def apply(_arr: List[Data]) = new DArr(_arr)

  /** Create a new DArr from an Arr */
  def apply(_arr: Arr) =
    new DArr(_arr.arr.map(DataImplicits.valueToData).toList)

/** Wrapper for Str */
final class DStr(private val _str: String) extends Data:

  /** Get the underlying string */
  def str = _str

  /** Return a new DStr with the new string */
  def str_=(s: String) = DStr(s)

  override def toString(): String = _str

/** Factory methods for constructing a DStr */
object DStr:
  /** Construct a DStr from a given String */
  def apply(_str: String) = new DStr(_str)

  /** Construct a DStr from a given Str */
  def apply(_str: Str) = new DStr(_str.str)

/** Wrapper for Num */
final class DNum(private val _num: BigDecimal) extends Data:
  /** Get the underlying BigDecimal */
  def num = _num

  /** Return a new DNum with the updated BigDecimal */
  def num_=(n: BigDecimal) = DNum(n)

  override def toString(): String = _num.toString

/** Factory methods for constructing a DNum */
object DNum:
  /** Construct a DNum from a Decimal */
  def apply(_num: BigDecimal) = new DNum(_num)

  /** Construct a DNum from a Num */
  def apply(_num: Num) = new DNum(_num.num)

/** Wrapper for Bool */
final class DBool(private val _bool: Boolean) extends Data:
  /** Get the underlying Boolean */
  def bool = _bool

  /** Return a new DBool with the updated Boolean */
  def bool_=(b: Boolean) = DBool(b)

  override def toString(): String = _bool.toString

/** Factory methods for constructing a DBool */
object DBool:
  /** Construct a DNum from a Boolean */
  def apply(_bool: Boolean) = new DBool(_bool)

  /** Construct a DNum from a DBool */
  def apply(_bool: Bool) = new DBool(_bool.bool)

/** Wrapper for Null */
object DNull extends Data:
  override def toString(): String = "null"

/** Provides implicit convertions from Obj To Data, and from Data to primitives
  */
object DataImplicits:

  implicit def dobjToObj(dobj: DObj): mutable.Map[String, Data] = dobj.obj
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
