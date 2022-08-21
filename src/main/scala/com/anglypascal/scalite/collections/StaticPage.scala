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
import com.rallyhealth.weejson.v1.Obj

class StaticPage(
    parentDir: String,
    relativePath: String,
    globals: DObj,
    collection: DObj
) extends Item(parentDir, relativePath, globals, collection)
    with Page:
  /** */

  protected val parentName: String = "default"

  val title: String =
    front_matter.extractOrElse("title")(
      front_matter.extractOrElse("name")(
        getFileName(filepath)
      )
    )

  def locals: DObj =
    val dateFormat =
      front_matter.extractOrElse("dateFormat")(
        globals.getOrElse("dateFormat")("yyyy-MM-dd")
      )
    val obj = Obj(
      "title" -> title,
      "outputExt" -> outputExt,
      "modifiedTime" -> lastModifiedTime(filepath, dateFormat),
      "filename" -> getFileName(filepath)
    )
    DObj(obj)

  private lazy val permalinkTemplate =
    front_matter.extractOrElse("permalinkTemplate")(
      globals
        .getOrElse("collection")(DObj())
        .getOrElse("permalinkTemplate")(
          globals.getOrElse("permalinkTemplate")(
            Defaults.staticFilesPermalink
          )
        )
    )
  protected lazy val permalink = purifyUrl(URL(permalinkTemplate)(locals))

  protected lazy val outputExt =
    front_matter.extractOrElse("outputExt")(
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

  lazy val visible: Boolean = front_matter.extractOrElse("visible")(true)

object StaticPage extends ItemConstructor[StaticPage]:
  def apply(
      parentDir: String,
      relativePath: String,
      globals: DObj,
      collection: DObj
  ): StaticPage =
    new StaticPage(parentDir, relativePath, globals, collection)

object StaticPages extends Collection[StaticPage](StaticPage)("statics")
