package com.anglypascal.scalite.utils

import com.anglypascal.mustache.{AST, ASTConverter}
import com.anglypascal.mustache.Mustache
import scala.language.implicitConversions
// import com.rallyhealth.weejson.v1.{Value, Obj, Arr, Str, Num, Bool, Null}

/** AST Support for the Data implementation to be used in Mustache */
private[scalite] class DataAST(v: Data) extends AST:

  import DataAST.*

  def findKey(key: String): Option[Any] =
    v match
      case obj: DObj => obj.get(key).map(dataToAST)
      case null      => None
      case other     => Some(value)

  def value: Any =
    v match
      case obj: DObj  => obj.obj.toMap.map(p => (p._1, dataToAST(p._2)))
      case arr: DArr  => arr.arr.toSeq.map(dataToAST)
      case str: DStr  => str.str
      case num: DNum  => num.num
      case boo: DBool => boo.bool
      case _          => None

private[scalite] object DataAST extends ASTConverter:

  implicit def dataToAST(data: Data): AST = new DataAST(data)

  def toAST(context: Any): Either[Any, AST] =
    context match
      case c: Data => Right(c)
      case other   => Left(other)

  def canHandle(context: Any): Boolean =
    context match
      case c: Data => true
      case _       => false

// object TestingData:

//   @main
//   def dataTest =
//     val dd = DataAST
//     val m = new Mustache("{{#name}}{{b}}{{/name}}")
//     val d = DObj(Obj("name" -> Arr(Obj("b" -> "1"), Obj("b" -> "2"))))

//     println(m.render(d))
