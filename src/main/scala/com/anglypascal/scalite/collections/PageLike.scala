package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.anglypascal.scalite.utils.StringProcessors.slugify
import com.rallyhealth.weejson.v1.Obj

class PageLike(val rType: String)(
    val parentDir: String,
    val relativePath: String,
    globals: DObj,
    collection: DObj
) extends Element
    with Page:

  lazy val identifier = filepath

  protected val layoutName: String = Defaults.Statics.layout

  lazy val title: String =
    frontMatter.extractOrElse("title")(
      frontMatter.extractOrElse("name")(
        getFileName(filepath)
      )
    )

  def locals: DObj =
    val dateFormat =
      frontMatter.extractOrElse("dateFormat")(
        globals.getOrElse("dateFormat")(Defaults.dateFormat)
      )
    val obj = Obj(
      "title" -> title,
      "outputExt" -> outputExt,
      "modifiedTime" -> lastModifiedTime(filepath, dateFormat),
      "filename" -> getFileName(filepath),
      "collection" -> collection.getOrElse("name")("statics"),
      "slugTitle" -> slugify(title)
    )
    DObj(obj)

  lazy val permalink =
    val permalinkTemplate =
      frontMatter.extractOrElse("permalink")(
        globals
          .getOrElse("collection")(DObj())
          .getOrElse("permalink")(
            globals.getOrElse("permalink")(
              Defaults.Statics.permalink
            )
          )
      )
    purifyUrl(URL(permalinkTemplate)(locals))

  protected lazy val outputExt =
    frontMatter.extractOrElse("outputExt")(
      Converters
        .findByExt(filepath)
        .map(_.outputExt)
        .getOrElse(".html")
    )

  protected lazy val render: String =
    val str = Converters.convert(mainMatter, filepath)
    val context = DObj(
      "site" -> globals,
      "page" -> locals
    )
    layout match
      case Some(l) => l.render(context, str)
      case None    => str

  lazy val visible: Boolean = frontMatter.extractOrElse("visible")(true)

  override def toString(): String =
    Console.CYAN + title + Console.RESET

object PageConstructor extends ElemConstructor:
  def apply(rType: String)(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj
  ): Element =
    new PageLike(rType)(parentDir, relativePath, globals, collection)
