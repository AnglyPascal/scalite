package com.anglypascal.scalite.collections

import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.DateParser.lastModifiedTime
import com.anglypascal.scalite.utils.DirectoryReader.getFileName
import com.anglypascal.scalite.utils.StringProcessors.purifyUrl
import com.anglypascal.scalite.utils.StringProcessors.slugify
import com.rallyhealth.weejson.v1.Obj

class StaticPage(
    parentDir: String,
    relativePath: String,
    globals: DObj,
    collection: DObj,
    rType: String = "statics"
) extends Item(parentDir, relativePath, globals, collection, rType)
    with Page:
  /** */

  protected val parentName: String = Defaults.Statics.layout

  val title: String =
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

  protected lazy val permalink =
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
    val str = Converters.convert(main_matter, filepath)
    val context = DObj(
      "site" -> globals,
      "page" -> locals
    )
    parent match
      case Some(l) => l.render(context, str)
      case None    => str

  lazy val visible: Boolean = frontMatter.extractOrElse("visible")(true)

object StaticPage extends ItemConstructor[StaticPage]:
  def apply(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj,
      rType: String
  ): StaticPage =
    new StaticPage(parentDir, relativePath, globals, collection, rType)

object StaticPages extends Collection[StaticPage](StaticPage)("statics")
