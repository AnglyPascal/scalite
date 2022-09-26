package com.anglypascal.scalite.data.mutable

import com.anglypascal.scalite.data.immutable
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

/** Mutable JSON-like collection */
sealed trait Data extends Ordered[Data]:

  /** If this is a DStr, return the string */
  def getStr: Option[String] = None

  /** If this is a DNum, return the number */
  def getNum: Option[BigDecimal] = None

  /** If this is a DBool, return the boolean */
  def getBool: Option[Boolean] = None

  /** If this is a DArr, return the DArr */
  def getDArr: Option[DArr] = None

  /** If this is a DObj, return the DObj */
  def getDObj: Option[DObj] = None

  protected[data] def toString(depth: Int): String = toString()

/** Immutable wrapper around Obj. Provides only one mutable entry for content
  * for performance reasons.
  */
final class DObj(private val obj: Map[String, Data])
    extends Data
    with Map[String, Data]:

  override def getDObj: Option[DObj] = Some(this)

  /** Returns the value stored against key in the underlying Map */
  override def contains(key: String) = obj.contains(key: String)

  /** Returns the value stored against key in the underlying Map */
  override def apply(key: String): Data = obj(key)

  /** Returns the value mapped to key wrapped in an Option */
  def get(key: String): Option[Data] = obj.get(key)

  override def addOne(pairs: (String, Data)): this.type =
    obj.addOne(pairs)
    this

  override def +=(pairs: (String, Any)): this.type =
    obj.addOne(pairs._1 -> DataImplicits.fromAny(pairs._2))
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

  def getOrElse(key: String)(default: DObj): DObj =
    get(key).flatMap(_.getDObj).getOrElse(default)

  def getOrElse(key: String)(default: DArr): DArr =
    get(key).flatMap(_.getDArr).getOrElse(default)

  def getOrElse(key: String)(default: Data): Data =
    get(key).getOrElse(default)

  def extractOrElse(key: String)(default: String): String =
    remove(key).flatMap(_.getStr).getOrElse(default)

  def extractOrElse(key: String)(default: Boolean): Boolean =
    remove(key).flatMap(_.getBool).getOrElse(default)

  def extractOrElse(key: String)(default: BigDecimal): BigDecimal =
    remove(key).flatMap(_.getNum).getOrElse(default)

  def extractOrElse(key: String)(default: DObj): DObj =
    remove(key).flatMap(_.getDObj).getOrElse(default)

  def extractOrElse(key: String)(default: DArr): DArr =
    remove(key).flatMap(_.getDArr).getOrElse(default)

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
              case _       => ()
          case n: DNum =>
            that(key) match
              case nm: Num => this(key) = DNum(nm)
              case _       => ()
          case b: DBool =>
            that(key) match
              case bl: Bool => this(key) = DBool(bl)
              case _        => ()
          case a: DArr =>
            that(key) match
              case ar: Arr => this(key) = DArr(ar)
              case _       => ()
          case o: DObj =>
            that(key) match
              case ob: Obj => o.update(ob)
              case _       => ()
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
              case ob: DObj =>
                o.update(ob)
              case _ => ()
          case _ => ()
    this

  def update(that: immutable.DObj): this.type =
    DataImplicits.fromIObj(that) match
      case v: DObj => this update v
      case _       => this

  def copy: DObj =
    val nObj = DObj()
    for (k, v) <- this do
      v match
        case v: DStr  => nObj += k -> DStr(v.str)
        case v: DNum  => nObj += k -> DNum(v.num)
        case v: DBool => nObj += k -> DBool(v.bool)
        case v: DArr  => nObj += k -> v.copy
        case v: DObj  => nObj += k -> v.copy
        case _        => nObj += k -> DNull
    nObj

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
final class DArr(private val arr: ArrayBuffer[Data])
    extends Data
    with Buffer[Data]:

  override def getDArr: Option[DArr] = Some(this)

  /** Return the index entry of the List[Data], inefficient TODO */
  def apply(ind: Int): Data = arr(ind)

  /** Add a data entry to the front of the list, returning a new DArr */
  def addOne(entry: Data): this.type =
    arr += entry
    this

  def addOne(entry: Any): this.type =
    arr += DataImplicits.fromAny(entry)
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

  def copy: DArr =
    val nArr = DArr()
    for v <- this do
      v match
        case v: DStr  => nArr += DStr(v.str)
        case v: DNum  => nArr += DNum(v.num)
        case v: DBool => nArr += DBool(v.bool)
        case v: DArr  => nArr += v.copy
        case v: DObj  => nArr += v.copy
        case _        => nArr += DNull
    nArr

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

  override def getStr: Option[String] = Some(_str)

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

  override def getNum: Option[BigDecimal] = Some(_num)

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

  override def getBool: Option[Boolean] = Some(_bool)

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
      case any: Int        => DNum(any)
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

  given fromIObj: Conversion[immutable.Data, Data] =
    _ match
      case v: immutable.DObj =>
        DObj(v.map(p => (p._1, fromIObj(p._2))).toSeq: _*)
      case v: immutable.DArr  => DArr(v.map(d => fromIObj(d)).toSeq: _*)
      case v: immutable.DStr  => DStr(v.str)
      case v: immutable.DNum  => DNum(v.num)
      case v: immutable.DBool => DBool(v.bool)
      case immutable.DNull    => DNull

/** FEATURE: Add wrappers for lambda functions. Text lambda AST with
  * mustache.Then define the predefined filter functions in terms of these
  * lambda
  */
