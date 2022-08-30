package com.anglypascal.scalite.collections

import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.utils.StringProcessors.titleParser
import com.rallyhealth.weejson.v1.Obj

class ItemLike(val rType: String)(
    val parentDir: String,
    val relativePath: String,
    globals: DObj,
    collection: DObj
) extends Element:

  /** Title of this item */
  val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(
        titleParser(filepath).getOrElse("untitled" + this.toString)
      )
    ) // so that titles are always different for different items

  // TODO: check with jekyll if it needs more default variables
  lazy val locals =
    val used = List("title")
    val obj = Obj()
    for
      (s, v) <- frontMatter.obj
      if !used.contains(s)
    do obj(s) = v
    obj.obj ++= List("title" -> title)
    DObj(obj)

  lazy val visible: Boolean = frontMatter.extractOrElse("visible")(false)

  /** If there's some front\_matter, then the main\_matter will be conerted with
    * appropriate converter. Otherwise, the identity will be returned
    */
  protected lazy val render: String =
    // TODO what if frontmatter is deleted by the process
    if frontMatter.obj.isEmpty then mainMatter
    else Converters.convert(mainMatter, filepath)

def itemConstructor(rType: String)(
    parentDir: String,
    relativePath: String,
    globals: DObj,
    collection: DObj
): Element =
  new ItemLike(rType)(parentDir, relativePath, globals, collection)
