package com.anglypascal.scalite.trees

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.mutable.Data

class CategoryTree(
    catType: String,
    treeName: String,
    _parent: Option[PostTree]
)(_configs: MObj, _globals: IObj)
    extends PostTree(catType, treeName, _parent)(_configs, _globals):

  /** */
  def createChild(name: String): CategoryTree =
    CategoryTree(catType, name, Some(this))(_configs, _globals)

  lazy val locals: IObj =
    val temp = MObj(
      "type" -> treeType,
      "url" -> permalink,
      "outputExt" -> outputExt,
      "path" -> pathToRootNames.mkString("/")
    )
    temp update _configs
    IObj(temp)

  def getPaths(post: PostLike): Iterable[List[String]] =

    def dataToPath(data: Data): Iterable[List[String]] =
      data match
        case s: DStr =>
          s.str.trim.split(",").map(_.trim.split("/").toList)
        case a: DArr => a.flatMap(dataToPath(_)).toList
        // FIXME: can't figure out a way to handle objects. We need to find a way to
        // specify the nodes where we want to add our post.
        // case o: MObj =>
        //   o.map((k, v) => dataToPath(v).map(k :: _)).flatten
        case _ => List()

    dataToPath(post.getTreesList(catType))

object CategoryStyle extends TreeStyle[PostLike]:

  val styleName: String = "category"

  def apply(treeType: String)(
      configs: MObj,
      globals: IObj
  ): Tree[PostLike] =
    new CategoryTree(treeType, treeType, None)(configs, globals)
