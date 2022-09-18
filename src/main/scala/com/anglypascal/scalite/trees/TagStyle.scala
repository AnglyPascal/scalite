package com.anglypascal.scalite.trees

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.collections.PostLike

class TagTree(
    tagType: String,
    treeName: String,
    _parent: Option[PostTree]
)(_configs: MObj, _globals: IObj)
    extends PostTree(tagType, treeName, _parent)(_configs, _globals):

  /** */
  def createChild(name: String): TagTree =
    TagTree(tagType, name, Some(this))(_configs, _globals)

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
    val unslugged =
      post.getTreesList(tagType) match
        case s: DStr => s.str.trim.split(",").flatMap(_.trim.split(" "))
        case a: DArr =>
          a.arr.flatMap(_.getStr).flatMap(_.trim.split(" ")).toArray
        case _ => Array[String]()
    unslugged.map(_ :: List())

object TagStyle extends TreeStyle[PostLike]:

  val styleName: String = "tag"

  def apply(treeType: String)(
      configs: MObj,
      globals: IObj
  ): Tree[PostLike] =
    new TagTree(treeType, treeType, None)(configs, globals)
