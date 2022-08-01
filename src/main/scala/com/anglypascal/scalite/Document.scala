package com.anglypascal.scalite

import scala.io.Source
import scala.util.matching.Regex
import _root_.com.rallyhealth.weejson.v1._

trait Document(filename: String) extends Page:
  /** check this regex later for whitespace
    */
  private val src = readFile(filename)
  private val yaml_regex = raw"\A---\n?([\s\S\n]*)---\n?([\s\S\n]*)".r

  /** this is inefficient. write a custom parser that cuts the front matter and
    * returns the leftover source
    */
  val (obj, content) =
    val raw = src.toString
    raw match
      case yaml_regex(a, b) => (yamlParser(a), b)
      case _                => (Obj(), raw)

  val parent_name =
    if obj.obj.contains("layout") then
      obj("layout") match
        case s: Str => s.str
        case _      => ""
    else ""

  def render(context: Obj, partials: Map[String, Layout]): String =
    val str = eval(context, partials)
    parent match
      case l: Layout =>
        context("content") = str
        l.render(context, partials)
      case null => str

  def eval(context: Obj, partials: Map[String, Layout]): String
