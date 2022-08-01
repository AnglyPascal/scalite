package com.anglypascal.scalite

import converters.convert
import com.rallyhealth.weejson.v1.*

/** Reads the content of a post file and prepares a Page object for that.
  *
  * @param filename
  *   path to the post file
  */
class Post(filename: String) extends Document(filename):

  /** Get the title of the post from the front matter, defaulting back to the
    * title parsed from the filename.
    */
  def title: String =
    if obj.obj.contains("title") then
      obj("title") match
        case s: Str => s.str
        case _      => titleParser(filename)
    else titleParser(filename)

  /** Convert the contents of the post to HTML, throwing an exception on failure
    * to do so
    *
    * TODO: there can be different types of exceptions
    */
  def eval(context: Obj, partials: Map[String, Layout]): String =
    convert(content, filename) match
      case Right(s) => s
      case Left(e)  => throw e

  // def addToCollections: Unit = ???
