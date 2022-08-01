package com.anglypascal.scalite

import converters.*
import _root_.com.rallyhealth.weejson.v1.*

class Post(filename: String) extends Document(filename):
  /** */

  def title: String =
    if obj.obj.contains("title") then
      obj("title") match
        case s: Str => s.str
        case _      => titleParser(filename)
    else titleParser(filename)

  def eval(context: Obj, partials: Map[String, Layout]): String =
    val converter = findConverter(filename)
    converter.convert(content) match
      case Right(s) => s
      case Left(e)  => throw e

  def addToCollections: Unit = ???
