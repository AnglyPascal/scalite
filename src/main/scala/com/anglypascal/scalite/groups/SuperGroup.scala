package com.anglypascal.scalite.groups

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.data.DataExtensions.getChain

import com.anglypascal.scalite.ScopedDefaults
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.URL

import scala.collection.mutable.LinkedHashMap

/** Creates a new type of Group.
  *
  * @param style
  *   A GroupStyle object defining how the PostsGroup objects are created from
  *   PostLike objects
  * @param globals
  *   Immutable DObj containing the global setting for this site
  */
final class SuperGroup1(private val cons: GroupConstructor)(
    val gType: String,
    private val configs: MObj,
    private val globals: IObj
) extends Page:

  private val scopedDefaults = ScopedDefaults.getDefaults("", gType)

  private val _configs: MObj = configs.copy update scopedDefaults

  private lazy val style = cons(gType, configs, globals)

  lazy val permalink =
    val permalinkTemplate =
      _configs.getOrElse("baseLink")(Defaults.Tags.baseLink)

    val urlObj = MObj(
      "gType" -> gType
    )
    urlObj.update(_configs)
    purifyUrl(URL(permalinkTemplate)(IObj(urlObj)))

  /** PostsGroup objects of this style */
  protected val groups = LinkedHashMap[String, PostsGroup]()

  lazy val identifier: String = permalink

  protected lazy val outputExt: String = 
    _config.getOrElse("outputExt")(Defaults.PostsGroup.outputExt)

  protected lazy val render: String = ???

  val visible: Boolean = ???

  protected val layoutName: String = ???

  /** Defines how posts add themselves to this group type. Usually it's by a
    * combination of specifying group names in the front matter as a string or
    * list, or by indiciating group names in the filename or inside a class
    * variable.
    *
    * @param post
    *   A post object to be added to the groups of this type
    */
  def addPostToGroups(post: PostLike): Unit =
    // names of categories this post belongs to
    val catNames = style.getGroupNames(post)
    // for each category, add this post to it and add this category back to the post
    for cat <- catNames do
      groups.get(cat) match
        case Some(t) =>
          t.addPost(post)
        case None =>
          groups(cat) = style.groupConstructor(cat)
          groups(cat).addPost(post)

  def process(dryRun: Boolean = false): Unit =
    for (_, grp) <- groups do grp.write(dryRun)
    // write(dryRun)

  override def toString(): String =
    BLUE(style.getClass.getSimpleName) + ": " + groups
      .map(_._2.toString)
      .mkString(", ")
