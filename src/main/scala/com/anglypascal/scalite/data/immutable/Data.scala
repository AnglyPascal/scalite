package com.anglypascal.scalite.data.immutable

import com.anglypascal.scalite.data.mutable
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
  final def getArr: Option[DArr] =
    this match
      case data: DArr => Some(data)
      case _          => None

  /** If this is a DObj, return the map */
  final def getObj: Option[DObj] =
    this match
      case data: DObj => Some(data)
      case _          => None

  protected[data] def toString(depth: Int): String = toString()

  def mut: mutable.Data

/** Immutable wrapper around Obj. Provides only one mutable entry for content
  * for performance reasons.
  */
final class DObj(private val obj: Map[String, Data])
    extends Data
    with Map[String, Data]:

  def mut: mutable.DObj = mutable.DObj(obj.map((k, v) => (k, v.mut)).toSeq: _*)

  /** Returns the value stored against key in the underlying Map */
  override def contains(key: String) = obj.contains(key: String)

  /** Returns the value stored against key in the underlying Map */
  override def apply(key: String): Data = obj(key)

  /** Returns the value mapped to key wrapped in an Option */
  def get(key: String): Option[Data] = obj.get(key)

  /** Return a new DObj object with the given pair added */
  def add(pairs: (String, Any)*): DObj =
    DObj(obj ++ pairs.map((k, v) => (k, DataImplicits.fromAny(v))))

  /** Get an iterable for the list of keys in the map */
  override def keys = obj.keys

  /** Remove the keys from the map returning the new map */
  def removeAll(keys: List[String]): DObj = DObj(obj -- keys)

  def removed(key: String): DObj = DObj(obj.removed(key))

  def updated[V >: Data](k: String, v: V): DObj =
    try DObj(obj.updated(k, v.asInstanceOf[Data]))
    catch
      case e: java.lang.ClassCastException =>
        throw java.lang.ClassCastException("DObj can only accept Data values")
      case e => throw e

  def iterator = obj.iterator

  def getOrElse(key: String)(default: String): String =
    get(key).flatMap(_.getStr).getOrElse(default)

  def getOrElse(key: String)(default: Boolean): Boolean =
    get(key).flatMap(_.getBool).getOrElse(default)

  def getOrElse(key: String)(default: BigDecimal): BigDecimal =
    get(key).flatMap(_.getNum).getOrElse(default)

  def getOrElse(key: String)(default: DObj): DObj =
    get(key).flatMap(_.getObj).map(DObj(_)).getOrElse(default)

  def getOrElse(key: String)(default: DArr): DArr =
    get(key).flatMap(_.getArr).map(DArr(_)).getOrElse(default)

  def compare(that: Data): Int = 0

  def update(that: mutable.DObj): DObj =
    val o = obj ++ that.immut
    DObj(o)

  override def toString(): String = toString(0)

  override protected[data] def toString(depth: Int): String =
    "  " * depth + Console.GREEN + "{\n" + Console.RESET +
      obj
        .map((k, v) =>
          "  " * (depth + 1) + Console.RED + k + Console.YELLOW
            + ": " + Console.RESET + v.toString(depth + 1)
        )
        .mkString(
          "\n"
        ) + Console.GREEN + "\n" + "  " * depth + "}" + Console.RESET

/** Companion object to provide factory constructors. */
object DObj:

  def apply(_obj: Map[String, Data]) = new DObj(_obj)

  def apply(_obj: scala.collection.mutable.Map[String, Data]) =
    new DObj(_obj.toMap)

  def apply(pairs: (String, Any)*) =
    new DObj(pairs.map(p => (p._1, DataImplicits.fromAny(p._2))).toMap)

  def apply(_obj: Obj): DObj =
    DataImplicits.fromValue(_obj).getObj.get

  def apply(_obj: mutable.DObj): DObj = _obj.immut

/** Immutable wrapper around Arr */
final class DArr(private val arr: List[Data]) extends Data with Iterable[Data]:

  def mut: mutable.DArr = mutable.DArr(arr.map(_.mut): _*)

  /** Return the index entry of the List[Data], inefficient TODO */
  def apply(ind: Int): Data = arr(ind)

  /** Add a data entry to the front of the list, returning a new DArr */
  def add(entry: Data): DArr = DArr(entry :: arr)

  def iterator = arr.iterator

  def compare(that: Data): Int = 0

  override def toString(): String =
    Console.GREEN + "[ " + Console.RESET + arr.mkString(", ") +
      Console.GREEN + " ]" + Console.RESET

/** Companion object to DArr to provide factory constructors */
object DArr:

  def apply(_arr: List[Data]) = new DArr(_arr)

  def apply(_arr: Any*) =
    new DArr(List(_arr.map(DataImplicits.fromAny): _*))

  def apply(_arr: Arr) =
    new DArr(_arr.arr.map(DataImplicits.fromValue).toList)

  def apply(dobj: mutable.DArr) = dobj.immut

/** Wrapper for Str */
final class DStr(val str: String) extends Data:

  def mut: mutable.DStr = mutable.DStr(str)

  /** Add a string to this DStr */
  def +(nstr: String) = DStr(str + nstr)

  /** Add the string of another DStr to this DStr */
  def +(dstr: DStr) = DStr(str + dstr.str)

  override def toString(): String =
    "\"" + Console.BLUE + str + Console.RESET + "\""

  def compare(that: Data): Int =
    that match
      case that: DObj => -1
      case that: DArr => -1
      case that: DStr => str.compare(that.str)
      case _          => 0

  override def equals(that: Any): Boolean =
    that match
      case that: DStr => str == that.str
      case _          => false

/** Factory methods for constructing a DStr */
object DStr:
  def apply(_str: String) = new DStr(_str)
  def apply(_str: Str) = new DStr(_str.str)

/** Wrapper for Num */
final class DNum(val num: BigDecimal) extends Data:

  def mut: mutable.DNum = mutable.DNum(num)

  override def toString(): String =
    Console.GREEN + num.toString + Console.RESET

  def compare(that: Data): Int =
    that match
      case that: DObj => -1
      case that: DArr => -1
      case that: DNum => num.compare(that.num)
      case _          => 0

  override def equals(that: Any): Boolean =
    that match
      case that: DNum => num == that.num
      case _          => false

/** Factory methods for constructing a DNum */
object DNum:
  def apply(_num: BigDecimal) = new DNum(_num)
  def apply(_num: Num) = new DNum(_num.num)

/** Wrapper for Bool */
final class DBool(val bool: Boolean) extends Data:

  def mut: mutable.DBool = mutable.DBool(bool)

  override def toString(): String =
    Console.YELLOW + bool.toString + Console.RESET

  def compare(that: Data): Int =
    that match
      case that: DObj  => -1
      case that: DArr  => -1
      case that: DBool => bool.compare(that.bool)
      case _           => 0

  override def equals(that: Any): Boolean =
    that match
      case that: DBool => bool == that.bool
      case _           => false

/** Factory methods for constructing a DBool */
object DBool:
  def apply(_bool: Boolean) = new DBool(_bool)
  def apply(_bool: Bool) = new DBool(_bool.bool)

/** Wrapper for Null */
object DNull extends Data:

  def mut: mutable.DNull.type = mutable.DNull

  override def toString(): String = "null"

  def compare(that: Data): Int =
    that match
      case DNull => 0
      case _     => -1

/** Provides implicit convertions from Obj To Data, and from Data to primitives
  */
object DataImplicits:

  given fromAny: Conversion[Any, Data] =
    _ match
      case any: String       => DStr(any)
      case any: Int          => DNum(any)
      case any: BigDecimal   => DNum(any)
      case any: Boolean      => DBool(any)
      case any: Data         => any
      case any: mutable.Data => any.immut
      case any: Value        => fromValue(any)
      case _                 => DNull

  given fromValue: Conversion[Value, Data] =
    _ match
      case v: Obj  => DObj(v.obj)
      case v: Arr  => DArr(v.arr)
      case v: Str  => DStr(v.str)
      case v: Num  => DNum(v.num)
      case v: Bool => DBool(v.bool)
      case Null    => DNull
