package com.anglypascal.scalite.data.mutable

import com.anglypascal.mustache.AST
import com.anglypascal.mustache.ASTConverter
import com.anglypascal.mustache.Mustache

import scala.language.implicitConversions

/** AST Support for the Data implementation to be used in Mustache */
final class DataAST(v: Data) extends AST:

  import DataAST.dataToAST

  def findKey(key: String): Option[Any] =
    v match
      case obj: DObj => obj.get(key).map(dataToAST)
      case null      => None
      case other     => Some(dataToAST(v))

  def value: Any =
    v match
      case obj: DObj  => obj.obj.toMap.map(p => (p._1, dataToAST(p._2)))
      case arr: DArr  => arr.arr.toSeq.map(dataToAST)
      case str: DStr  => str.str
      case num: DNum  => num.num
      case boo: DBool => boo.bool
      case _          => None

object DataAST extends ASTConverter:

  given dataToAST: Conversion[Data, AST] = new DataAST(_)

  def toAST(context: Any): Either[Any, AST] =
    context match
      case c: Data => Right(c)
      case other   => Left(other)

  def canHandle(context: Any): Boolean =
    context match
      case c: Data => true
      case _       => false
