package com.anglypascal.scalite.trees

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.documents.SourceFile

class CategoryTree[A <: Renderable with WithTree[A] with SourceFile](
    val treeType: String,
    val treeName: String,
    protected val parent: Option[AnyTree[A]]
)(_configs: MObj, protected val globals: IObj)
    extends AnyTree[A](_configs):

  /** */
  def createChild(name: String): CategoryTree[A] =
    CategoryTree(treeType, name, Some(this))(_configs, globals)

  def getPaths(post: A): Iterable[List[String]] =
    val rP = post.relativePath.split("/").init.toList

    def dataToPath(data: Data): Iterable[List[String]] =
      data match
        case s: DStr => s.str.trim.split(",").map(_.trim.split("/").toList)
        case a: DArr => a.flatMap(dataToPath(_)).toList
        case o: MObj => o.flatMap((k, v) => dataToPath(v).map(k :: _))
        case _       => Iterable()

    dataToPath(post.getTreesList(treeType))

class CategoryStyle[A <: Renderable with WithTree[A] with SourceFile]
    extends TreeStyle[A]("category"):
  def apply(treeType: String)(configs: MObj, globals: IObj): Tree[A] =
    CategoryTree(treeType, treeType, None)(configs, globals)
