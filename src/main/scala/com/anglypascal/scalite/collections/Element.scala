package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.documents.Reader
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
trait Element extends Reader with Renderable:

  lazy val identifier = filepath

  /** An Element also has some internal variables that are publicly visible */
  lazy val locals: DObj

/** Compare two given Elements by the given key */
def compareBy(
    fst: Element,
    snd: Element,
    key: String
): Int =
  val s = cmpOpt(fst.locals.get(key), fst.locals.get(key))
  if s != 0 then return s
  val n = cmpOpt(fst.locals.get("title"), fst.locals.get("title"))
  if n != 0 then return n
  0
