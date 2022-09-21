package com.anglypascal.scalite.collections

import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.documents.SourceFile

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

  import com.anglypascal.scalite.documents.Reader

  lazy val identifier = filepath

  val rType: String

  protected val frontMatter = Reader.frontMatter(rType, filepath)

  protected lazy val mainMatter = Reader.mainMatter(filepath)

  protected lazy val shouldConvert =
    frontMatter.getOrElse("shouldConvert")(true)
