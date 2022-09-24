package com.anglypascal.scalite.trees

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Generator
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.hooks.TreeHooks
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
  protected val _trees = LinkedHashMap[String, Tree[A]]()
  def trees = _trees.toList.map(_._2)

  /** Add a new SuperGroup to this site */
  protected def addTree(tree: Tree[A]): Unit =
    _trees += tree.treeType -> tree

  /** Create pages for each PostsGroup that wishes to be rendered */
  protected[trees] def process(dryRun: Boolean = false): Unit =
    for (_, tree) <- _trees do
      logger.debug(s"processing Tree ${tree.treeType}")
      tree.process(dryRun)

  def addToForests(post: A): Unit

  /** Read the configurations and create necessary SuperGroups */
  def apply(configs: MObj, globals: IObj): Unit =
    configs update TreeHooks.beforeInits(globals)(IObj(configs))
    val conf = defaultConfig update configs
    for (key, value) <- conf do
      value match
        case value: MObj =>
          val style = value.extractOrElse("style")(Defaults.Tree.defaultStyle)
          val tType = value.extractOrElse("type")(Defaults.Tree.defaultType)
          logger.debug(s"adding new Forest $tType of style $style")
          styles.get(style) match
            case Some(treeStyle) => addTree(treeStyle(tType)(value, globals))
            case None            => logger.warn(s"TreeStyle $style not found")
        case _ => logger.warn(s"configuration for Tree $key is empty")

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

  override def toString(): String =
    _forests.map(_.toString()).mkString("\n")
