package com.anglypascal.scalite.groups

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import com.anglypascal.scalite.plugins.GroupHooks

/** Group defines a group of Renderables. A Renderable can add itself to
  * multiple Groups. Group's job is to provide a `process` method that does
  * something with the Renderables of this Group, maybe render them into a table
  * of contents like Page, or provide the metadata of the Renderables to some
  * other object.
  *
  * A Group is itself a Renderable, so it can be rendered into a resulting
  * String, and can have a Layout.
  */
trait Group[A <: Renderable] extends Renderable:

  protected val logger = Logger(s"${GREEN(groupName)}[$groupType]")

  /** Contains the Renderables of this Group */
  private val _items = LinkedHashMap[String, A]()
  def items = _items.toList.map(_._2)

  protected val globals: IObj 

  protected val configs: MObj 

  /** Name of the Group, which will be called by the Renderables wishing to add
    * itself to this.
    */
  val groupName: String

  /** Type of the Group. For example, "tags" or "categories" are the two default
    * types of Groups.
    */
  val groupType: String

  /** Add the given Renderable
    *
    * @param key
    *   The key to map this item to
    * @param item
    *   The item to add to this group
    */
  def add(key: String, item: A) = _items += key -> item

  /** Returns the item stored against the key */
  def get(key: String) = _items.get(key)

  /** Processes the items, usually either writing them to a Page, or giving the
    * metadata about the items to another object.
    */
  protected[groups] def process(dryRun: Boolean = false): Unit

  override def toString(): String =
    s"${GREEN(groupName)}[$groupType]: \n" +
      items.map("    " + _.toString).mkString("\n")

/** SuperGroup is a Group of Groups of the same type.
  */
trait SuperGroup[A <: Renderable] extends Group[Group[A]] with Renderable:

  protected override val logger =
    Logger("SuperGroup " + BLUE(groupType.capitalize))

  /** Maps the given item to the given key in all the Groups mentioned by this
    * item that are in this SuperGroup.
    *
    * Fetches the Group names this item mentions using the `getGroupNames`
    * method. Creates new Group as needed using the `createGroup` method.
    */
  def addItem(key: String, item: A) =
    logger.trace(s"adding $item to SuperGroup $groupType")
    getGroupNames(item) foreach { grp =>
      get(grp) match
        case Some(group) => group.add(key, item)
        case None =>
          val group = createGroup(grp)
          group.add(key, item)
          add(grp, group)
    }

  /** Given the name, create a new PostsGroup */
  def createGroup(name: String): Group[A]

  /** Process the names of the Groups this Renderable item belongs to.
    *
    * @param item
    *   an item of type A to be added
    * @return
    *   an iterator with all the names of the Groups of type Group[A]
    */
  def getGroupNames(item: A): Iterable[String]

  override def toString(): String =
    BLUE(groupType) + "\n" + items.map("  " + _.toString).mkString("\n")

/** GroupStyle provides the logic to construct a new SuperGroup. */
trait GroupStyle[A <: Renderable] extends Plugin:

  /** Name of this style */
  val styleName: String

  /** Given group type, group configs and global configs, create a new
    * SuperGroup
    *
    * @param groupType
    *   Type of the group, like "tags" or "categories"
    * @param configs
    *   Mutable DObj containing customization set in groups/gType section
    * @param globals
    *   Immutable DObj containing global configuration
    * @returns
    *   New GroupStyle conforming to these configurations
    */
  def apply(groupType: String, configs: MObj, globals: IObj): SuperGroup[A]

  override def toString(): String = RED(styleName)
