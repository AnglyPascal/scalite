package com.anglypascal.scalite.collections

import com.anglypascal.scalite.documents.SourceFile
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.utils.cmpOpt

/** Trait defining an element of a Collection. This can be a post, a static
  * page, or some small object to be rendered as part of a separate page.
  *
  * An Element is a Reader, so it can read files from the filepath and can
  * separate out the frontMatter and the mainMatter
  *
  * An Element is also a Renderable, so it can have a parent layout and it can
  * be rendered into some HTML string.
  */
trait Element extends Renderable with SourceFile:

  lazy val identifier = filepath

/** Compare two given Elements by the given keys */
def compareBy(fst: Element, snd: Element, key1: String, keys: String*): Int =
  val s = cmpOpt(fst.locals.get(key1), snd.locals.get(key1))
  if s != 0 then s
  else
    for key <- keys do
      val t = cmpOpt(fst.locals.get(key), snd.locals.get(key))
      if t != 0 then return t
    cmpOpt(fst.locals.get("title"), snd.locals.get("title"))
