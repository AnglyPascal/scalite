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

  lazy val locals: IObj = ???

class CategoryTreeRoot(catType: String)(_configs: MObj, _globals: IObj)
    extends CategoryTree(catType, catType, None)(_configs, _globals)
    with RootNode[PostLike]:

  def getPaths(post: PostLike): Iterable[List[String]] =

    def dataToPath(data: Data): Iterable[List[String]] =
      data match
        case s: DStr =>
          List(s.str.trim.split(",").flatMap(_.trim.split(" ")).toList)
        case a: DArr => a.flatMap(dataToPath(_)).toList
        case o: MObj =>
          o.map((k, v) => dataToPath(v).map(k :: _)).flatten
        case _ => List()

    dataToPath(post.getGroupsList(catType))

object CategoryStyle extends TreeStyle[PostLike]:

  val styleName: String = "categories"

  def apply(treeType: String)(
      configs: MObj,
      globals: IObj
  ): RootNode[PostLike] =
    new CategoryTreeRoot(treeType)(configs, globals)
