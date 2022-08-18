package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.converters.Converter
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DObj
import com.anglypascal.scalite.data.DStr
import com.anglypascal.scalite.documents.LayoutObject
import com.anglypascal.scalite.documents.Layouts
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFiles
import com.typesafe.scalalogging.Logger

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import scala.reflect.ClassTag

/** Object that is called with the plugins directory and the plugins data from
  * config.yml to add all the available plugins to runtime
  */
object PluginManager:

  private val logger = Logger("Plugin Manager")

  /** Rerturns the URLClassLoader from the jar file from jarPath */
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
        logger.error("SecurityException was thrown with message " + e.toString)
        None
      case e =>
        logger.error(s"Loading $jarPath threw " + e.toString)
        None

  /** Returns the object from the classLoader if it exists */
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
    catch case e => None

  /** Search for the given object in all the loaded jars */
  private def findObject[T <: Plugin](objName: (DStr | DObj))(using
      ClassTag[T],
      ClassTag[Plugin]
  ): Option[Plugin] =
    val key = objName match
      case str: DStr => str.str
      case obj: DObj =>
        obj.keys.headOption match
          case None =>
            logger.error("Plugin customization had wrong syntax.")
            return None
          case some => some.get

    def getObj = getObject[T](key)
    for loader <- classLoaders do
      getObj(loader) match
        case (some: Some[T]) =>
          objName match
            case k: DStr =>
              logger.debug(s"Found plugin $key")
              return some
            case obj: DObj =>
              logger.debug(
                s"Found plugin $key " +
                  "and initiated with the customization options"
              )
              return some.map(_.addConfigs(obj(key)))
        case _ => ()
    logger.error(
      s"Plugin $objName not found inside module com.anglypascal.scalite.plugins"
    )
    None

  /** Find the objects of type T in the jars */
  private def findObjects[T <: Plugin](names: List[DStr | DObj])(using
      ClassTag[T]
  ) = names.map(findObject[T]).filter(_ == None).map(_.get)

  private def loadConverters(names: List[DStr | DObj]) =
    findObjects[Converter](names).map(C =>
      Converters.addConverter(C.asInstanceOf[Converter])
    )

  private def loadCollections(names: List[DStr | DObj]) =
    findObjects[Collection[?]](names).map(C =>
      Collections.addToCollection(C.asInstanceOf[Collection[?]])
    )

  private def loadLayouts(names: List[DStr | DObj]) =
    findObjects[LayoutObject](names).map(L =>
      Layouts.addEngine(L.asInstanceOf[LayoutObject])
    )

  private var classLoaders = List[URLClassLoader]()

  /** Create classLoaders from all the jar files in the pluginsDir */
  private def getClassLoaders(pluginsDir: String): Unit =
    classLoaders = getListOfFiles(pluginsDir)
      .filter(_.endsWith(".jar"))
      .map(loadJar)
      .filter(_ != None)
      .map(_.get)

  def apply(pluginsDir: String, pluginsData: DObj): Unit =
    getClassLoaders(pluginsDir)

    def getArrStr(key: String): List[DStr | DObj] =
      if pluginsData.contains(key) then
        pluginsData(key) match
          case d: DArr =>
            d.filter(_.isInstanceOf[DStr]).map(_.asInstanceOf[DStr]) ++
              d.filter(_.isInstanceOf[DObj]).map(_.asInstanceOf[DObj])
          case _ => List()
      else List()

    loadConverters(getArrStr("converters"))
    loadCollections(getArrStr("collections"))
    loadLayouts(getArrStr("layouts"))
