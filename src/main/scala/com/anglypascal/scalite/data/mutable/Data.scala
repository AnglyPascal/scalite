package com.anglypascal.scalite.data.mutable

import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Bool
import com.rallyhealth.weejson.v1.Null
import com.rallyhealth.weejson.v1.Num
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import scala.Conversion
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map

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
  final def getArr: Option[ArrayBuffer[Data]] =
    this match
      case data: DArr => Some(data.arr)
      case _          => None

  /** If this is a DObj, return the map */
  final def getObj: Option[Map[String, Data]] =
    this match
      case data: DObj => Some(data.obj)
      case _          => None

  /** If this is a DStr, return the string */
  final def extractStr: Option[String] =
    this match
      case data: DStr => Some(data.str)
      case _          => None

  /** If this is a DNum, return the number */
  final def extractNum: Option[BigDecimal] =
    this match
      case data: DNum => Some(data.num)
      case _          => None

  /** If this is a DBool, return the boolean */
  final def extractBool: Option[Boolean] =
    this match
      case data: DBool => Some(data.bool)
      case _           => None

  /** If this is a DArr, return the list */
  final def extractArr: Option[ArrayBuffer[Data]] =
    this match
      case data: DArr => Some(data.arr)
      case _          => None

  /** If this is a DObj, return the map */
  final def extractObj: Option[Map[String, Data]] =
    this match
      case data: DObj => Some(data.obj)
      case _          => None

  protected[data] def toString(depth: Int): String = toString()

/** Immutable wrapper around Obj. Provides only one mutable entry for content
  * for performance reasons.
  */
final class DObj(val obj: Map[String, Data])
    extends Data
    with Map[String, Data]:

  /** Returns the value stored against key in the underlying Map */
  override def contains(key: String) = obj.contains(key: String)

  /** Returns the value stored against key in the underlying Map */
  override def apply(key: String): Data = obj(key)

  /** Returns the value mapped to key wrapped in an Option */
  def get(key: String): Option[Data] = obj.get(key)

  override def addOne(pairs: (String, Data)): this.type =
    obj.addOne(pairs)
    this

  /** Get an iterable for the list of keys in the map */
  override def keys = obj.keys

  override def subtractOne(elem: String): this.type =
    obj.subtractOne(elem)
    this

  def iterator = obj.iterator

  def getOrElse(key: String)(default: String): String =
    get(key).flatMap(_.getStr).getOrElse(default)

  def getOrElse(key: String)(default: Boolean): Boolean =
    get(key).flatMap(_.getBool).getOrElse(default)

  def getOrElse(key: String)(default: BigDecimal): BigDecimal =
    get(key).flatMap(_.getNum).getOrElse(default)

  def getOrElse(key: String)(default: ArrayBuffer[Data]): ArrayBuffer[Data] =
    get(key).flatMap(_.getArr).getOrElse(default)

  def getOrElse(key: String)(default: Map[String, Data]): Map[String, Data] =
    get(key).flatMap(_.getObj).getOrElse(default)

  def getOrElse(key: String)(default: DObj): DObj =
    get(key).flatMap(_.getObj).map(DObj(_)).getOrElse(default)

  def getOrElse(key: String)(default: DArr): DArr =
    get(key).flatMap(_.getArr).map(DArr(_)).getOrElse(default)

  def getOrElse(key: String)(default: Data): Data =
    get(key).getOrElse(default)

  def extractOrElse(key: String)(default: String): String =
    remove(key).flatMap(_.extractStr).getOrElse(default)

  def extractOrElse(key: String)(default: Boolean): Boolean =
    remove(key).flatMap(_.extractBool).getOrElse(default)

  def extractOrElse(key: String)(default: BigDecimal): BigDecimal =
    remove(key).flatMap(_.extractNum).getOrElse(default)

  def extractOrElse(key: String)(
      default: ArrayBuffer[Data]
  ): ArrayBuffer[Data] =
    remove(key).flatMap(_.extractArr).getOrElse(default)

  def extractOrElse(key: String)(
      default: Map[String, Data]
  ): Map[String, Data] =
    remove(key).flatMap(_.extractObj).getOrElse(default)

  def extractOrElse(key: String)(default: DObj): DObj =
    remove(key).flatMap(_.extractObj).map(DObj(_)).getOrElse(default)

  def extractOrElse(key: String)(default: DArr): DArr =
    remove(key).flatMap(_.extractArr).map(DArr(_)).getOrElse(default)

  def extractOrElse(key: String)(default: Data): Data =
    remove(key).getOrElse(default)

  def update(that: Obj): this.type =
    for key <- that.obj.keys do
      if !contains(key) then this(key) = DataImplicits.fromValue(that(key))
      else
        this(key) match
          case s: DStr =>
            that(key) match
              case st: Str => this(key) = DStr(st)
              case _        => ()
          case n: DNum =>
            that(key) match
              case nm: Num => this(key) = DNum(nm)
              case _        => ()
          case b: DBool =>
            that(key) match
              case bl: Bool => this(key) = DBool(bl)
              case _         => ()
          case a: DArr =>
            that(key) match
              case ar: Arr => this(key) = DArr(ar)
              case _        => ()
          case o: DObj =>
            that(key) match
              case ob: Obj => o.update(ob)
              case _        => ()
          case _ => ()
    this

  def update(that: DObj): this.type =
    for key <- that.keys do
      if !contains(key) then this(key) = that(key)
      else
        this(key) match
          case s: DStr =>
            that(key) match
              case st: DStr => this(key) = st
              case _        => ()
          case n: DNum =>
            that(key) match
              case nm: DNum => this(key) = nm
              case _        => ()
          case b: DBool =>
            that(key) match
              case bl: DBool => this(key) = bl
              case _         => ()
          case a: DArr =>
            that(key) match
              case ar: DArr => this(key) = ar
              case _        => ()
          case o: DObj =>
            that(key) match
              case ob: DObj => o.update(ob)
              case _        => ()
          case _ => ()
    this

  def compare(that: Data): Int = 0

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

  def apply(pairs: (String, Any)*) =
    new DObj(Map(pairs.map(p => (p._1, DataImplicits.fromAny(p._2))): _*))

  def apply(_obj: Obj) =
    new DObj(_obj.obj.map((k, v) => (k, DataImplicits.fromValue(v))))

/** Mutable wrapper around Arr */
final class DArr(val arr: ArrayBuffer[Data]) extends Data with Buffer[Data]:

  /** Return the index entry of the List[Data], inefficient TODO */
  def apply(ind: Int): Data = arr(ind)

  /** Add a data entry to the front of the list, returning a new DArr */
  def addOne(entry: Data): this.type =
    arr += entry
    this

  def clear() = arr.clear

  def insert(idx: Int, elem: Data): Unit = arr.insert(idx, elem)

  def insertAll(idx: Int, elems: IterableOnce[Data]): Unit =
    arr.insertAll(idx, elems)

  def iterator = arr.iterator

  def length = arr.length

  def patchInPlace(
      from: Int,
      patch: IterableOnce[Data],
      replaced: Int
  ): this.type =
    arr.patchInPlace(from, patch, replaced)
    this

  def prepend(elem: Data): this.type =
    arr.prepend(elem)
    this

  def remove(idx: Int): Data = arr.remove(idx)

  def remove(idx: Int, count: Int): Unit = arr.remove(idx, count)

  def update(idx: Int, elem: Data): Unit = arr.update(idx, elem)

  def compare(that: Data): Int = 0

  override def toString(): String =
    Console.GREEN + "[ " + Console.RESET + arr.mkString(", ") +
      Console.GREEN + " ]" + Console.RESET

/** Companion object to DArr to provide factory constructors */
object DArr:

  def apply(_arr: ArrayBuffer[Data]) = new DArr(_arr)

  def apply(_arr: Any*) =
    new DArr(ArrayBuffer(_arr.map(DataImplicits.fromAny): _*))

  def apply(_arr: Arr) = new DArr(_arr.arr.map(DataImplicits.fromValue))

/** Wrapper for Str */
final class DStr(private var _str: String) extends Data:

  def str = _str
  def str_=(s: String) = _str = s

  /** Add a string to this DStr */
  def +(nstr: String) =
    _str += nstr
    this

  /** Add the string of another DStr to this DStr */
  def +(dstr: DStr) =
    _str += dstr.str
    this

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
final class DNum(private var _num: BigDecimal) extends Data:

  def num = _num
  def num_=(n: BigDecimal) = _num = n

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
final class DBool(private var _bool: Boolean) extends Data:

  def bool = _bool
  def bool_(b: Boolean) = _bool = b

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
  override def toString(): String = "null"

  def compare(that: Data): Int =
    that match
      case DNull => 0
      case _     => -1

/** Provides implicit convertions from Obj To Data, and from Data to primitives
  */
object DataImplicits:

  given fromDStr: Conversion[DStr, String] = _.str
  given fromDNum: Conversion[DNum, BigDecimal] = _.num
  given fromDBool: Conversion[DBool, Boolean] = _.bool

  given fromString: Conversion[String, DStr] = DStr(_)
  given fronBigDecima: Conversion[BigDecimal, DNum] = DNum(_)
  given fromBoolean: Conversion[Boolean, DBool] = DBool(_)
  given fromAny: Conversion[Any, Data] = any =>
    any match
      case any: String     => DStr(any)
      case any: BigDecimal => DNum(any)
      case any: Boolean    => DBool(any)
      case any: Data       => any
      case _               => DNull

  given fromValue: Conversion[Value, Data] =
    _ match
      case v: Obj  => DObj(v)
      case v: Arr  => DArr(v)
      case v: Str  => DStr(v)
      case v: Num  => DNum(v)
      case v: Bool => DBool(v)
      case Null    => DNull

/** FEATURE: Add wrappers for lambda functions. Text lambda AST with
  * mustache.Then define the predefined filter functions in terms of these
  * lambda
  */
