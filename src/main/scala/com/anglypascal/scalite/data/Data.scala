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
sealed trait Data extends Ordered[Data]:

  /** If this is a DStr, return the string */
  final def getStr: Option[String] =
    this match
      case data: DStr => Some(data.str)
      case _          => None

  /** If this is a DNum, return the number */
  final def getNum: Option[BigDecimal] =
    this match
      case data: DNum => Some(data.num)
      case _          => None

  /** If this is a DBool, return the boolean */
  final def getBool: Option[Boolean] =
    this match
      case data: DBool => Some(data.bool)
      case _           => None

  /** If this is a DArr, return the list */
  final def getArr: Option[List[Data]] =
    this match
      case data: DArr => Some(data._arr)
      case _          => None

  /** If this is a DObj, return the map */
  final def getObj: Option[Map[String, Data]] =
    this match
      case data: DObj => Some(data._obj)
      case _          => None

  protected[data] def toString(depth: Int): String = toString()

/** Immutable wrapper around Obj. Provides only one mutable entry for content
  * for performance reasons.
  */
final class DObj(private[data] val _obj: Map[String, Data])
    extends Data
    with Iterable[(String, Data)]:

  /** Get an iterable for the list of keys in the map */
  def keys = _obj.keys

  /** Remove the keys from the map returning the new map */
  def removeAll(keys: List[String]): DObj = DObj(_obj -- keys)

  def removed(key: String): DObj = DObj(_obj.removed(key))

  /** Returns a new DObj with the given mutable Map */
  def obj_=(o: Map[String, Data]) = DObj(o)

  /** Returns a new DObj with the given Obj */
  def obj_=(o: Obj) = DObj(o)

  /** Returns the value stored against key in the underlying Map */
  def contains(key: String) = _obj.contains(key: String)

  /** Returns the value stored against key in the underlying Map */
  def apply(key: String): Data = _obj(key)

  /** Returns the value mapped to key wrapped in an Option */
  def get(key: String): Option[Data] = _obj.get(key)

  /** Return a new DObj object with the given pair added */
  def add(pairs: (String, Data)*): DObj = DObj(_obj ++ pairs)

  def iterator = _obj.iterator

  /** Apply the given function to the underlying map */
  def map[A, B >: Data](f: ((String, B)) => A): Iterable[A] = _obj.map(f)

  /** Returns the string stored against the key, returning the default if fails
    */
  def getOrElse(key: String)(default: String): String =
    get(key).flatMap(_.getStr) match
      case Some(s) => s
      case _       => default

  /** Returns the boolean stored against the key, returning the default if fails
    */
  def getOrElse(key: String)(default: Boolean): Boolean =
    get(key).flatMap(_.getBool) match
      case Some(s) => s
      case _       => default

  /** Returns the number stored against the key, returning the default if fails
    */
  def getOrElse(key: String)(default: BigDecimal): BigDecimal =
    get(key).flatMap(_.getNum) match
      case Some(s) => s
      case _       => default

  /** Returns the list stored against the key, returning the default if fails
    */
  def getOrElse(key: String)(default: List[Data]): List[Data] =
    get(key).flatMap(_.getArr) match
      case Some(s) => s
      case _       => default

  /** Returns the map stored against the key, returning the default if fails
    */
  def getOrElse(key: String)(
      default: Map[String, Data]
  ): Map[String, Data] =
    get(key).flatMap(_.getObj) match
      case Some(s) => s
      case _       => default

  def getOrElse(key: String)(
      default: DObj
  ): DObj =
    get(key).flatMap(_.getObj) match
      case Some(s) => DObj(s)
      case _       => default

  def compare(that: Data): Int = 0

  override def toString(): String = toString(0)

  override protected[data] def toString(depth: Int): String =
    "  " * depth + Console.GREEN + "{\n" + Console.RESET +
      _obj
        .map((k, v) =>
          "  " * (depth + 1) + Console.RED + k + Console.YELLOW
            + " -> " + Console.RESET + v.toString(depth + 1)
        )
        .mkString(
          "\n"
        ) + Console.GREEN + "\n" + "  " * depth + "}" + Console.RESET

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
final class DArr(private[data] val _arr: List[Data])
    extends Data
    with Iterable[Data]:

  /** Return a new DArr with the new List[Data] */
  def arr_=(a: List[Data]) = DArr(a)

  /** Return the index entry of the List[Data], inefficient TODO */
  def apply(ind: Int): Data = _arr(ind)

  /** Add a data entry to the front of the list, returning a new DArr */
  def add(entry: Data): DArr = DArr(entry :: _arr)

  def iterator = _arr.iterator

  def map[A, B >: Data](f: B => A): List[A] = _arr.map(f)

  def filter[B >: Data](f: B => Boolean): List[Data] = _arr.filter(f)

  def compare(that: Data): Int = 0

  override def toString(): String =
    Console.GREEN + "[ " + Console.RESET + _arr.mkString(", ") +
      Console.GREEN + " ]" + Console.RESET

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

  override def toString(): String =
    "\"" + Console.BLUE + _str + Console.RESET + "\""

  def compare(that: Data): Int =
    that match
      case that: DObj => -1
      case that: DArr => -1
      case that: DStr => _str.compare(that._str)
      case _          => 0

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

  override def toString(): String =
    Console.GREEN + _num.toString + Console.RESET

  def compare(that: Data): Int =
    that match
      case that: DObj => -1
      case that: DArr => -1
      case that: DNum => _num.compare(that._num)
      case _          => 0

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

  override def toString(): String =
    Console.YELLOW + _bool.toString + Console.RESET

  def compare(that: Data): Int =
    that match
      case that: DObj  => -1
      case that: DArr  => -1
      case that: DBool => _bool.compare(that._bool)
      case _           => 0

/** Factory methods for constructing a DBool */
object DBool:

  /** Construct a DNum from a Boolean */
  def apply(_bool: Boolean) = new DBool(_bool)

  /** Construct a DNum from a DBool */
  def apply(_bool: Bool) = new DBool(_bool.bool)

/** Wrapper for Null */
object DNull extends Data:
  override def toString(): String = "null"

  def compare(that: Data): Int =
    that match
      case DNull => 0
      case _     => -1

/** Provides implicit convertions from Obj To Data, and from Data to primitives
  */
object DataImplicits:

  // implicit def dobjToObj(dobj: DObj): mutable.Map[String, Data] = dobj.obj
  given Conversion[DStr, String] = _.str
  given Conversion[DNum, BigDecimal] = _.num
  given Conversion[DBool, Boolean] = _.bool

  given Conversion[String, DStr] = DStr(_)
  given Conversion[BigDecimal, DNum] = DNum(_)
  given Conversion[Boolean, DBool] = DBool(_)

  given Conversion[Value, Data] =
    _ match
      case v: Obj  => DObj(v)
      case v: Arr  => DArr(v)
      case v: Str  => DStr(v)
      case v: Num  => DNum(v)
      case v: Bool => DBool(v)
      case Null    => DNull
