package com.anglypascal.scalite.trees

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

class CategoryTree(
    val treeType: String,
    val treeName: String,
    protected val parent: Option[PostTree]
)(_configs: MObj, protected val globals: IObj)
    extends PostTree(_configs):

  /** */
  def createChild(name: String): CategoryTree =
    CategoryTree(treeType, name, Some(this))(_configs, globals)

  def getPaths(post: PostLike): Iterable[List[String]] =
    val rP = post.relativePath.split("/").init.toList

    def dataToPath(data: Data): Iterable[List[String]] =
      data match
        case s: DStr => s.str.trim.split(",").map(_.trim.split("/").toList)
        case a: DArr => a.flatMap(dataToPath(_)).toList
        case o: MObj => o.flatMap((k, v) => dataToPath(v).map(k :: _))
        case _       => Iterable()

    dataToPath(post.getTreesList(treeType))

object CategoryStyle extends TreeStyle[PostLike]("category"):
  def apply(treeType: String)(configs: MObj, globals: IObj): Tree[PostLike] =
    CategoryTree(treeType, treeType, None)(configs, globals)
