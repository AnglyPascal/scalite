package com.anglypascal.scalite

import scala.io.Source
import scala.util.matching.Regex
import _root_.com.rallyhealth.weejson.v1._

trait Document(filename: String):
  /** check this regex later for whitespace 
   */
  private val src = readFile(filename)
  private val yaml_regex = raw"\A---\n?([\s\S\n]*)---\n?([\s\S\n]*)".r

  /** this is inefficient. write a custom parser that cuts the front matter and returns
   *  the leftover source 
   */
  val (obj, content) = 
    val raw = src.toString
    raw match 
      case yaml_regex(a, b) => (yamlParser(a), b)
      case _ => (Obj(), raw)

  private var parent_name =
    if obj.obj.contains("layout") then
      obj("layout") match
        case s: Str => s.str
        case _      => ""
    else ""

  private var _parent: Layout = null
  def parent = _parent

  def set_parent(map: Map[String, Layout]): Unit =
    map.get(parent_name) match
      case Some(l) => _parent = l
      case _       => _parent = null

  def render(context: Obj, partials: Map[String, Layout]): String =
    val str = eval(context, partials)
    parent match
      case l: Layout =>
        context("content") = str
        l.render(context, partials)
      case null => str

  def eval(context: Obj, partials: Map[String, Layout]): String
