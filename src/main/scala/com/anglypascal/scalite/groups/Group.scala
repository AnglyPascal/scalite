package com.anglypascal.scalite.groups

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.plugins.Plugin

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer

trait Group[A <: Renderable] extends Renderable:

  private val _items = ListBuffer[A]()
  lazy val items = _items.toList

  val groupName: String

  val groupType: String

  def add(item: A) = _items += item

  lazy val locals: IObj

  def process(dryRun: Boolean = false): Unit

trait SuperGroup[A <: Renderable] extends Group[Group[A]] with Renderable:

  private val groups = LinkedHashMap[String, Group[A]]()
  override lazy val items = groups.toList.map(_._2)

  def add(item: A) =
    getGroupNames(item) foreach { grp =>
      groups.get(grp) match
        case Some(group) => group.add(item)
        case None =>
          val group = createGroup(grp)
          group.add(item)
          groups += grp -> group
    }

  /** Given the name, create a new PostsGroup */
  def createGroup(name: String): Group[A]

  /** Process the names of the PostsGroups this PostLike belongs to by examining
    * it's frontMatter entry
    *
    * @param post
    *   a PostLike to be added to the this PostGroup style
    * @return
    *   an iterator with all the names of the PostGroups of this style
    */
  def getGroupNames(item: A): Iterable[String]

/** Constructs a new GroupStyle. To add custom GroupStyle, one needs to provide
  * an implementaiton of this constructor
  */
trait GroupConstructor[A <: Renderable] extends Plugin:

  /** Name of this GroupStyle */
  val styleName: String

  /** Given group type, group configs and global configs, create a new
    * GroupStyle
    *
    * @param gType
    *   String name of the type of the group, like "tags"
    * @param configs
    *   Mutable DObj containing customization set in groups/gType section
    * @param globals
    *   Immutable DObj containing global configuration
    * @returns
    *   New GroupStyle conforming to these configurations
    */
  def apply(gType: String, configs: MObj, globals: IObj): SuperGroup[A]

trait Groups[A <: Renderable] extends Configurable:

  private val styles: LinkedHashMap[String, GroupConstructor[A]] =
    LinkedHashMap()

  protected lazy val groupsConfig: MObj

  /** Add a new GroupConstructor for a new GroupStyle to this site */
  def addNewGroupStyle(style: GroupConstructor[A]) =
    styles += style.styleName -> style

  protected val groupTypes: LinkedHashMap[String, SuperGroup[A]] =
    LinkedHashMap()

  /** Add a new Group to this site */
  protected def addNewGroup(gType: String, group: SuperGroup[A]) =
    groupTypes += gType -> group

  /** Create pages for each PostsGroup that wishes to be rendered */
  def process(dryRun: Boolean = false): Unit =
    for (_, grp) <- groupTypes do grp.process(dryRun)

  /** Apply the configuration from groups section */
  def apply(configs: MObj, globals: IObj): Unit =
    groupsConfig.update(configs)
    for (key, value) <- groupsConfig do
      value match
        case value: MObj =>
          val style = value.extractOrElse("style")(Defaults.Group.defaultStyle)
          val gType = value.extractOrElse("gType")(Defaults.Group.defaultGType)
          addNewGroup(gType, styles(style)(gType, value, globals))
        case _ => ()
