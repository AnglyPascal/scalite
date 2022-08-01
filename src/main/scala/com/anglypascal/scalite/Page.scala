package com.anglypascal.scalite

import com.rallyhealth.weejson.v1.Obj

trait Page:
  val parent_name: String

  private var _parent: Layout = null
  def parent = _parent

  def set_parent(map: Map[String, Layout]): Unit =
    map.get(parent_name) match
      case Some(l) => _parent = l
      case _       => _parent = null

  /** method to output the file, needs to be abstract.
   *  for the sake of compilation rn, it's an unknown method
    */
  def write(filename: String): Unit = ???

  def render(context: Obj, partials: Map[String, Layout]): String 

