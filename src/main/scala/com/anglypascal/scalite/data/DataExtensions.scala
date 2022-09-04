package com.anglypascal.scalite.data

import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}

import scala.collection.mutable.Map
import com.typesafe.scalalogging.Logger

object DataExtensions:
  private val logger = Logger("Value extensions")

  def getChain(objs: (MObj | IObj)*)(key: String)(default: Boolean): Boolean =
    objs.toList match
      case Nil => default
      case obj :: tail =>
        obj match
          case obj: MObj =>
            obj.getOrElse(key)(getChain(tail: _*)(key)(default))
          case obj: IObj =>
            obj.getOrElse(key)(getChain(tail: _*)(key)(default))

  def getChain(objs: (MObj | IObj)*)(key: String)(default: String): String =
    objs.toList match
      case Nil => default
      case obj :: tail =>
        obj match
          case obj: MObj =>
            obj.getOrElse(key)(getChain(tail: _*)(key)(default))
          case obj: IObj =>
            obj.getOrElse(key)(getChain(tail: _*)(key)(default))

  def extractChain(objs: (MObj | IObj)*)(key: String)(default: Boolean): Boolean =
    objs.toList match
      case Nil => default
      case obj :: tail =>
        obj match
          case obj: MObj =>
            obj.extractOrElse(key)(getChain(tail: _*)(key)(default))
          case obj: IObj =>
            obj.getOrElse(key)(getChain(tail: _*)(key)(default))

  def extractChain(objs: (MObj | IObj)*)(key: String)(default: String): String =
    objs.toList match
      case Nil => default
      case obj :: tail =>
        obj match
          case obj: MObj =>
            obj.extractOrElse(key)(getChain(tail: _*)(key)(default))
          case obj: IObj =>
            obj.getOrElse(key)(getChain(tail: _*)(key)(default))


  // def update(that: Obj): Obj = 
  //   obj
