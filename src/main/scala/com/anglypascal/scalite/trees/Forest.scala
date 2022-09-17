package com.anglypascal.scalite.trees

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Generator
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.plugins.GroupHooks
import com.anglypascal.scalite.plugins.Plugin
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer

trait Forest[A <: Renderable] extends Configurable with Plugin:

  protected val logger = Logger("Forest")

  /** Avaiable TreeStyle implementations to this Forest */
  private val styles = LinkedHashMap[String, TreeStyle[A]]()

  /** Add a new TreeStyle to this Forest */
  def addTreeStyle(style: TreeStyle[A]): Unit =
    logger.debug(s"adding TreeStyle $style")
    styles += style.styleName -> style

  /** Default configs held by the Forest implementation */
  protected def defaultConfig: MObj

  /** SuperGroup objects held in this Forest */
  protected val _trees = LinkedHashMap[String, RootNode[A]]()
  def trees = _trees.toList.map(_._2)

  /** Add a new SuperGroup to this site */
  protected def addTree(tree: RootNode[A]): Unit =
    _trees += tree.treeType -> tree

  /** Create pages for each PostsGroup that wishes to be rendered */
  protected[trees] def process(dryRun: Boolean = false): Unit =
    for (_, tree) <- _trees do
      logger.debug(s"processing Tree ${tree.treeType}")
      tree.process(dryRun)

  /** Read the configurations and create necessary SuperGroups */
  def apply(configs: MObj, globals: IObj): Unit =
    GroupHooks.beforeInits.foldLeft(configs)((o, h) => h(globals)(IObj(o)))
    val conf = defaultConfig update configs
    for (key, value) <- conf do
      value match
        case value: MObj =>
          val style = value.extractOrElse("style")(Defaults.Group.defaultStyle)
          val gType = value.extractOrElse("gType")(Defaults.Group.defaultGType)
          logger.debug(s"adding new SuperGroup $gType of style $style")
          addTree(styles(style)(gType)(value, globals))
        case _ => ()

  override def toString(): String =
    _trees.map(_._2.toString).mkString("\n")

  def reset(): Unit =
    styles.clear()
    _trees.clear()


/** Holds all the Forest implementations avaiable */
object Forests:

  private val _forests = ListBuffer[Forest[?]](PostForests)
  def forests = _forests.toList

  def addForest(forest: Forest[?]) = _forests += forest

  def process(dryRun: Boolean = false): Unit =
    forests foreach { _.process(dryRun) }

  /** Cleans up the collections to start anew */
  protected[scalite] def reset(): Unit =
    _forests foreach { _.reset() }
    _forests.clear()
    _forests += PostForests
