package com.anglypascal.scalite.trees

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.hooks.TreeHooks
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap

/** Tree defines a tree containing Renderables of type A.
  *
  * Tree gives a generalized way to structure contents of the site into a
  * hierarchy. Tags and Categories are two examples of a Tree containing
  * PostLike objects. Tags and Categories are structured as below:
  *
  * ```
  *         tags
  *   _______|_______
  *   |      |      |
  *  tag1  tag4   tag5
  * ```
  * The Tags Tree has depth 1, and each node except the root may contain any
  * number of PostLike objects.
  *
  * ```
  *           categories
  *     __________|__________
  *     |         |         |
  *    cat1     cat4      cat5
  *  ___|___          ______|______
  *  |     |          |           |
  * cat2  cat3       cat6       cat10
  *               ____|____
  *               |       |
  *              cat7    cat8
  * ```
  * The Categories Tree is more general, and can be used to create taxonomical
  * structure of PostLike objects.
  *
  * It's possible to create a new Tree with a new treeType. A PostLike object
  * adds itself to a treeType by adding some data into its frontMatter under the
  * `treeType` variable. The data depends on the style of the Tree:
  *
  *   - **Tag style Trees**: The data might contain a space and comma separated
  *     string, or a list of strings
  *
  *   - **Category style Trees**: The data might be either of
  *     - A comma separated string or an array of string, with each entry being
  *       a path from the root of the Tree to the container of the PostLike
  *       object
  *     - An object giving the abstract tree structure that contains all the
  *       paths that the PostLike object might be inside
  */
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

  def addChild(key: String) =
    _children += key -> createChild(key)

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
          addChild(child)
          _children(child).add(key, item, p)

  def addItem(key: String, item: A): Unit =
    for path <- getPaths(item) do add(key, item, path)

  def getPaths(item: A): Iterable[List[String]]

  /** Returns the item stored against the key */
  def get(key: String) = _items.get(key)

  def pathToRoot: LazyList[Tree[A]] =
    this #:: parent.map(_.pathToRoot).getOrElse(LazyList())

  def pathToRootNames: LazyList[String] =
    pathToRoot.map(_.treeName)

  def dfs: LazyList[Tree[A]] =
    this #:: children.foldRight(LazyList[Tree[A]]())((l, r) => l.dfs #::: r)

  /** Processes the items, usually either writing them to a Page, or giving the
    * metadata about the items to another object.
    */
  protected[trees] def process(dryRun: Boolean = false): Unit

  override def toString(): String =
    toString("", "")

  protected def toString(ctab: String, itab: String): String =
    val iStr =
      if _items.isEmpty then itab + "| \n"
      else
        itab + _items
          .map(i => "[" + BLUE(i._1.toString) + "]")
          .mkString(", ") + "\n"

    ctab + s"${GREEN(treeName)} \n" + iStr +
      _children.map(_._2.toString(itab + "+-", itab + "| ")).mkString("")

/** Plugin that defines a new Tree from the given treeType, configurations and
  * global variables, returnin a RootNode instance
  */
abstract class TreeStyle[A <: Renderable](val styleName: String) extends Plugin:
  def apply(treeType: String)(configs: MObj, globals: IObj): Tree[A]
