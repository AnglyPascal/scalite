package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.Configurable
import com.anglypascal.scalite.Defaults
import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.collections.ElemConstructor
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.converters.ConverterConstructor
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.converters.Identity
import com.anglypascal.scalite.converters.Markdown
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.groups.Cluster
import com.anglypascal.scalite.groups.Clusters
import com.anglypascal.scalite.groups.GroupStyle
import com.anglypascal.scalite.groups.PostCluster
import com.anglypascal.scalite.layouts.LayoutGroupConstructor
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.layouts.MustacheLayouts
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.typesafe.scalalogging.Logger

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import scala.reflect.ClassTag

/** Load Plugin objects form the jar files in the plugins directory */
object PluginManager extends Configurable:

  private val logger = Logger("Plugin Manager")

  val sectionName: String = "plugins"

  /** Rerturns the URLClassLoader from jarPath */
  private def loadJar(jarPath: String): Option[URLClassLoader] =
    try
      Some(
        new URLClassLoader(
          Array[URL](new File(jarPath).toURI.toURL),
          classOf[Plugin].getClassLoader
        )
      )
    catch
      case e: SecurityException =>
        logger.error("SecurityException was thrown" + e.getMessage)
        None
      case e =>
        logger.error(s"Loading $jarPath threw " + e)
        None

  /** Returns the object objName from the classLoader if it exists */
  private def getObject[T <: Plugin](
      objName: String
  )(classLoader: URLClassLoader)(using man: ClassTag[T]): Option[T] =
    try
      val obj =
        classLoader
          .loadClass("com.anglypascal.scalite.plugins." + objName + "$")
          .getField("MODULE$")
          .get(man.runtimeClass)
      Some(obj.asInstanceOf[T])
      // TODO Check if there are any specifc exceptions being thorwn
    catch
      case e =>
        logger.trace(s"$objName wasn't in classLoader ${classLoader.toString}")
        None

  /** Search for the given object in all the loaded jars */
  private def findObject[T <: Plugin](objName: (String, MObj))(using
      ClassTag[T],
      ClassTag[Plugin]
  ): Option[Plugin] =
    val key = objName._1

    def getObj = getObject[T](key)
    for loader <- classLoaders do
      getObj(loader) match
        case some: Some[?] =>
          logger.debug(
            s"Found plugin $key " +
              "and initiated with the customization options"
          )
          return some.map(_.addConfigs(objName._2))
        case _ => ()
    logger.error(s"$objName not found in com.anglypascal.scalite.plugins")
    None

  /** Find the objects of type T in the jars */
  private def findObjects[T <: Plugin](names: Map[String, MObj])(using
      ClassTag[T]
  ) = names.map(findObject[T]).filter(_ == None).map(_.get)

  private def loadConverterConstructors(names: Map[String, MObj]) =
    findObjects[ConverterConstructor](names).map(C =>
      Converters.addConverterConstructor(C.asInstanceOf[ConverterConstructor])
    )

  private def loadElemConstructors(names: Map[String, MObj]) =
    findObjects[ElemConstructor](names).map(E =>
      Collections.addStyle(E.asInstanceOf[ElemConstructor])
    )

  private def loadGroupStyles(names: Map[String, MObj]) =
    findObjects[GroupStyle[PostLike]](names).map(C =>
      PostCluster.addGroupStyle(C.asInstanceOf[GroupStyle[PostLike]])
    )

  private def loadClusters(names: Map[String, MObj]) =
    findObjects[Cluster[?]](names).map(C =>
      Clusters.addCluster(C.asInstanceOf[Cluster[?]])
    )

  private def loadLayouts(names: Map[String, MObj]) =
    findObjects[LayoutGroupConstructor](names).map(L =>
      Layouts.addEngine(L.asInstanceOf[LayoutGroupConstructor])
    )

  private var classLoaders = List[URLClassLoader]()

  /** Create classLoaders from all the jar files in the pluginsDir */
  private def getClassLoaders(pluginsDir: String): Unit =
    classLoaders = getListOfFilepaths(pluginsDir)
      .filter(_.endsWith(".jar"))
      .map(loadJar)
      .filter(_ != None)
      .map(_.get)
      .toList

  def apply(pluginsData: MObj, globals: IObj): Unit =
    // Layouts.addEngine(MustacheLayouts)

    val pluginsDir: String =
      globals.getOrElse("base")(Defaults.Directories.base) +
        globals.getOrElse("pluginsDir")(Defaults.Directories.pluginsDir)

    getClassLoaders(pluginsDir)

    def getArr(key: String): Map[String, MObj] =
      def f(v: Data): Option[(String, MObj)] =
        v match
          case v: DStr => Some(v.str -> MObj())
          case v: MObj =>
            if v.obj.keys.toList.length == 1 then
              val k = v.obj.keys.head
              v(k) match
                case w: MObj => Some(k -> w)
                case _       => None
            else None
          case _ => None

      pluginsData.remove(key) match
        case Some(value) =>
          value match
            case v: DArr => v.arr.flatMap(f).toMap
            case _       => Map[String, MObj]()
        case None => Map[String, MObj]()

    loadConverterConstructors(getArr("converters"))
    loadElemConstructors(getArr("elementConstructors"))
    loadGroupStyles(getArr("groupStyles"))
    loadClusters(getArr("clusters"))
    loadLayouts(getArr("layouts"))
