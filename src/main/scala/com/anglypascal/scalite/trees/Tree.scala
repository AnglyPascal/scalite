package com.anglypascal.scalite.trees

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

trait Tree[A <: Renderable] extends Renderable:

  protected val logger = Logger(s"${GREEN(treeName)}[$treeType]")

  protected val globals: IObj

  protected val configs: MObj

  protected val parent: Option[Tree[A]]

  val treeName: String

  val treeType: String

  /** Contains the Renderables of this Group */
  private val _children = LinkedHashMap[String, Tree[A]]()
  def children = _children.toList.map(_._2)

  def addChild(key: String, child: Tree[A]) = _children += key -> child

  def createChild(name: String): Tree[A]

  def getChild(key: String) = _children.get(key)

  /** Contains the Renderables of this Group */
  private val _items = LinkedHashMap[String, A]()
  def items = _items.toList.map(_._2)

  /** Add the given Renderable
    *
    * @param key
    *   The key to map this item to
    * @param item
    *   The item to add to this tree
    */
  def add(key: String, item: A, path: List[String]): Unit =
    path match
      case Nil => _items += key -> item
      case child :: p =>
        if _children.contains(child) then _children(child).add(key, item, p)
        else
          addChild(child, createChild(child))
          _children(child).add(key, item, p)

  /** Returns the item stored against the key */
  def get(key: String) = _items.get(key)

  lazy val pathToRoot: List[Tree[A]] =
    var path = List[Tree[A]]()
    var now = this
    path = now :: path
    while now.parent != None do
      now = now.parent.get
      path = now :: path
    path.toList

  lazy val pathToRootNames: List[String] =
    var path = List[String]()
    var now = this
    path = now.treeName :: path
    while now.parent != None do
      now = now.parent.get
      path = now.treeName :: path
    path.toList

  /** Processes the items, usually either writing them to a Page, or giving the
    * metadata about the items to another object.
    */
  protected[trees] def process(dryRun: Boolean = false): Unit

  override def toString(): String =
    s"${GREEN(treeName)}[$treeType]: \n" +
      items.map("    " + _.toString).mkString("\n")

trait RootNode[A <: Renderable] extends Tree[A]:

  def addItem(key: String, item: A): Unit =
    for path <- getPaths(item) do add(key, item, path)

  def getPaths(item: A): Iterable[List[String]]

trait TreeStyle[A <: Renderable] extends Plugin:

  val styleName: String

  def apply(treeType: String)(configs: MObj, globals: IObj): RootNode[A]
