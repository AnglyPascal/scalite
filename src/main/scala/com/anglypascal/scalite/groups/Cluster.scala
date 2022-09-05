package com.anglypascal.scalite.groups

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.plugins.Plugin
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer

/** Cluster[A] provides a Configurable that handles SuperGroups of type
  * SuperGroup[A]. It holds a hash table of GroupStyles, reads configuration
  * settings and creates necessary SuperGroups with these styles.
  *
  * New GroupStyles might be added in the runtime using Plugins.
  */
trait Cluster[A <: Renderable] extends Configurable with Plugin:

  protected val logger = Logger("Cluster")

  /** Avaiable GroupStyle implementations to this Cluster */
  private val styles = LinkedHashMap[String, GroupStyle[A]]()

  /** Add a new GroupStyle to this Cluster */
  def addGroupStyle(style: GroupStyle[A]) =
    logger.debug(s"adding GroupStyle $style")
    styles += style.styleName -> style

  /** Default configs held by the Cluster implementation */
  protected lazy val defaultConfig: MObj

  /** SuperGroup objects held in this Cluster */
  protected val superGroups = LinkedHashMap[String, SuperGroup[A]]()

  /** Add a new SuperGroup to this site */
  protected def add(group: SuperGroup[A]) =
    superGroups += group.groupType -> group

  /** Create pages for each PostsGroup that wishes to be rendered */
  protected[groups] def process(dryRun: Boolean = false): Unit =
    for (_, grp) <- superGroups do
      logger.debug(s"processing SuperGroup ${grp.groupType}")
      grp.process(dryRun)

  /** Read the configurations and create necessary SuperGroups */
  def apply(configs: MObj, globals: IObj): Unit =
    defaultConfig.update(configs)
    for (key, value) <- defaultConfig do
      value match
        case value: MObj =>
          val style = value.extractOrElse("style")(Defaults.Group.defaultStyle)
          val gType = value.extractOrElse("gType")(Defaults.Group.defaultGType)
          logger.debug(s"adding new SuperGroup $gType of style $style")
          add(styles(style)(gType, value, globals))
        case _ => ()

  override def toString(): String =
    superGroups.map(_._2.toString).mkString("\n")

/** Holds all the Cluster implementations avaiable */
object Clusters:

  private val _clusters = ListBuffer[Cluster[?]](PostCluster)
  def clusters = _clusters.toList

  def addCluster(cluster: Cluster[?]) = _clusters += cluster

  def process(dryRun: Boolean): Unit =
    clusters foreach { _.process(dryRun) }
