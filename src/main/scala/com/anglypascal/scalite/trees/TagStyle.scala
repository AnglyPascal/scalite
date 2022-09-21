package com.anglypascal.scalite.trees

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Renderable

class TagTree[A <: Renderable with WithTree[A]](
    val treeType: String,
    val treeName: String,
    protected val parent: Option[AnyTree[A]]
)(_configs: MObj, protected val globals: IObj)
    extends AnyTree[A](_configs):

  /** */
  def createChild(name: String): TagTree[A] =
    TagTree(treeType, name, Some(this))(_configs, globals)

  def getPaths(post: A): Iterable[List[String]] =

    def dataToPath(data: Data): Iterable[String] =
      data match
        case s: DStr =>
          s.str.trim
            .split(",")
            .flatMap(
              _.trim match
                /** FIXME test this */
                case s if s.startsWith("\"") && s.endsWith("\"") => Array(s)
                case s                                           => s.split(",")
            )
        case a: DArr => a.flatMap(_.getStr)
        case _       => Iterable()

    dataToPath(post.getTreesList(treeType)).map(_ :: List())

class TagStyle[A <: Renderable with WithTree[A]]
    extends TreeStyle[A]("category"):
  def apply(treeType: String)(configs: MObj, globals: IObj): Tree[A] =
    TagTree(treeType, treeType, None)(configs, globals)
