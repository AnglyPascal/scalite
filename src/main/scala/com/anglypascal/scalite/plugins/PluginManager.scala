package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.collections.ElemConstructor
import com.anglypascal.scalite.converters.Converter
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DataExtensions.*
import com.anglypascal.scalite.groups.GroupConstructor
import com.anglypascal.scalite.groups.Groups
import com.anglypascal.scalite.layouts.LayoutObject
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Str
import com.rallyhealth.weejson.v1.Value
import com.typesafe.scalalogging.Logger

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import scala.collection.mutable.{Map => MMap}
import scala.reflect.ClassTag

/** Load Plugin objects form the jar files in the plugins directory */
object PluginManager:

  private val logger = Logger("Plugin Manager")

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
  private def findObject[T <: Plugin](objName: (String, Obj))(using
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
  private def findObjects[T <: Plugin](names: Map[String, Obj])(using
      ClassTag[T]
  ) = names.map(findObject[T]).filter(_ == None).map(_.get)

  private def loadConverters(names: Map[String, Obj]) =
    findObjects[Converter](names).map(C =>
      Converters.addConverter(C.asInstanceOf[Converter])
    )

  private def loadElemConstructors(names: Map[String, Obj]) =
    findObjects[ElemConstructor](names).map(E =>
      Collections.addStyle(E.asInstanceOf[ElemConstructor])
    )

  private def loadGroupConstructors(names: Map[String, Obj]) =
    findObjects[GroupConstructor](names).map(C =>
      Groups.addNewGroupStyle(C.asInstanceOf[GroupConstructor])
    )

  private def loadLayouts(names: Map[String, Obj]) =
    findObjects[LayoutObject](names).map(L =>
      Layouts.addEngine(L.asInstanceOf[LayoutObject])
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

  def apply(pluginsDir: String, pluginsData: MMap[String, Value]): Unit =
    getClassLoaders(pluginsDir)

    def getArr(key: String): Map[String, Obj] =
      def f(v: Value): Option[(String, Obj)] =
        v match
          case v: Str => Some(v.str -> Obj())
          case v: Obj =>
            if v.obj.keys.toList.length == 1 then
              val k = v.obj.keys.head
              v(k) match
                case w: Obj => Some(k -> w)
                case _      => None
            else None
          case _ => None

      pluginsData.remove(key) match
        case Some(value) =>
          value match
            case value: Arr => value.arr.flatMap(f).toMap
            case _          => Map[String, Obj]()
        case None => Map[String, Obj]()

    loadConverters(getArr("converters"))
    loadElemConstructors(getArr("elementConstructors"))
    loadGroupConstructors(getArr("groupConstructors"))
    loadLayouts(getArr("layouts"))
