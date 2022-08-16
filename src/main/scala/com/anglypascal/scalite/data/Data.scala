package com.anglypascal.scalite.data

import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Null
import com.rallyhealth.weejson.v1.Num
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import scala.Conversion

/** Immutable wrapper around WeeJson Value AST */
sealed trait Data

/** Immutable wrapper around Obj. Provides only one mutable entry for content
  * for performance reasons.
  */
final class DObj(private[data] val _obj: Map[String, Data]) extends Data:

  def keys = _obj.keys
  def remove(keys: List[String]): DObj =
    val obj = _obj -- keys
    DObj(obj)

  /** Returns a new DObj with the given mutable Map */
  def obj_=(o: Map[String, Data]) = DObj(o)

  /** Returns a new DObj with the given Obj */
  def obj_=(o: Obj) = DObj(o)

  /** Returns the value stored against key in the underlying Map */
  def contains(key: String) =
    _obj.contains(key: String) ||
      (key == "content" && _content != None)

  /** Returns the value stored against key in the underlying Map */
  def apply(key: String): Data =
    if key != "content" then _obj(key)
    else
      _content match
        case Some(c) => DStr(c)
        case None =>
          val a = this.toString
          Logger("DObj exception").warn(s"content is not set in DObj $a")
          DStr("")

  /** Returns the value stored against key in the underlying Map wrapped in
    * Option
    */
  def get(key: String): Option[Data] =
    if key != "content" then _obj.get(key)
    else _content.map(DStr(_))

  /** Return a new DObj object with the given pair added */
  def add(pair: (String, Data)): DObj =
    val _nobj = _obj + pair
    DObj(_nobj)

  /** Store "content" separately rather than in _obj to allow overwrite */
  def content: String = _content.getOrElse("")
  def content_=(c: String) = _content = Some(c)
  private var _content: Option[String] = None

  def map[A, B >: Data](f: ((String, B)) => A): Iterable[A] = _obj.map(f)

  override def toString(): String = _obj.toString

/** Companion object to provide factory constructors. */
object DObj:
  /** Construct a DObj from a mutable Map */
  def apply(_obj: Map[String, Data]) = new DObj(_obj)

  def apply(pairs: Tuple2[String, Data]*) = new DObj(Map(pairs: _*))

  /** Construct a DObj from an Obj */
  def apply(_obj: Obj) =
    new DObj(
      _obj.obj
        .map((k, v) => (k, DataImplicits.given_Conversion_Value_Data(v)))
        .toMap
    )

/** Immutable wrapper around Arr */
final class DArr(private[data] val _arr: List[Data]) extends Data:

  /** Return a new DArr with the new List[Data] */
  def arr_=(a: List[Data]) = DArr(a)

  /** Return the ind entry of the List[Data], inefficient TODO */
  def apply(ind: Int): Data = _arr(ind)

  /** Add a data entry to the front of the list, returning a new DArr */
  def add(entry: Data): DArr = DArr(entry :: _arr)

  def head: Data = _arr.head
  def tail: DArr = DArr(_arr.tail)

  def map[A, B >: Data](f: B => A): List[A] = _arr.map(f)

  def filter[B >: Data](f: B => Boolean): List[Data] = _arr.filter(f)

  override def toString(): String = _arr.mkString(", ")

/** Companion object to DArr to provide factory constructors */
object DArr:

  /** Create a new DArr from a List[Data] */
  def apply(_arr: List[Data]) = new DArr(_arr)

  def apply(_arr: Iterable[Data]) = new DArr(_arr.toList)

  /** Create a new DArr from an Arr */
  def apply(_arr: Arr) =
    new DArr(_arr.arr.map(DataImplicits.given_Conversion_Value_Data).toList)

/** Wrapper for Str */
final class DStr(private val _str: String) extends Data:

  /** Get the underlying string */
  def str = _str

  /** Return a new DStr with the new string */
  def str_=(s: String) = DStr(s)

  /** Add a string to this DStr */
  def +(nstr: String) = DStr(_str + nstr)

  /** Add the string of another DStr to this DStr */
  def +(dstr: DStr) = DStr(_str + dstr.str)

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

  // implicit def dobjToObj(dobj: DObj): mutable.Map[String, Data] = dobj.obj
  given Conversion[DStr, String] = _.str
  given Conversion[DNum, BigDecimal] = _.num
  given Conversion[DBool, Boolean] = _.bool

  given Conversion[Value, Data] =
    _ match
      case v: Obj  => DObj(v)
      case v: Arr  => DArr(v)
      case v: Str  => DStr(v)
      case v: Num  => DNum(v)
      case v: Bool => DBool(v)
      case Null    => DNull
