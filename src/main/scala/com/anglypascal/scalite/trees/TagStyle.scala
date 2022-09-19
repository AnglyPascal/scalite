package com.anglypascal.scalite.trees

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

class TagTree(
    val treeType: String,
    val treeName: String,
    protected val parent: Option[PostTree]
)(_configs: MObj, protected val globals: IObj)
    extends PostTree(_configs):

  /** */
  def createChild(name: String): TagTree =
    TagTree(treeType, name, Some(this))(_configs, globals)

  def getPaths(post: PostLike): Iterable[List[String]] =

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

object TagStyle extends TreeStyle[PostLike]("tag"):
  def apply(treeType: String)(configs: MObj, globals: IObj): Tree[PostLike] =
    TagTree(treeType, treeType, None)(configs, globals)
