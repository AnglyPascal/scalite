package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.converters.{Converter, Converters}
import com.anglypascal.scalite.collections.{Collection, Collections}
import com.anglypascal.scalite.documents.{LayoutObject, Layouts}
import com.anglypascal.scalite.utils.getListOfFiles
import com.anglypascal.scalite.data.DObj

import java.io.File
import java.net.{URL, URLClassLoader}
import scala.reflect.ClassTag
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.data.DArr
import com.anglypascal.scalite.data.DStr

/** Object that is called with the plugins directory and the plugins data from
  * config.yml to add all the available plugins to runtime
  */
object PluginManager:

  /** Dummy object to get the Plugin class variable */
  private object Plug extends Plugin

  private val logger = Logger("Plugin Manager")

  /** Rerturns the URLClassLoader from the jar file from jarPath */
  private def loadJar(jarPath: String): URLClassLoader =
    /** FIXME will throw error if Plugin not found in the jar */
    new URLClassLoader(
      Array[URL](new File(jarPath).toURI.toURL),
      Plug.getClass.getClassLoader
    )

  /** Returns the object from the classLoader if it exists */
  private def getObject[T](objName: String)(classLoader: URLClassLoader)(using
      man: ClassTag[T]
  ): Option[Object] =
    try
      val obj =
        classLoader
          .loadClass("com.anglypascal.scalite.plugins." + objName + "$")
          .getField("MODULE$")
          .get(man.runtimeClass)
      Some(obj)
    catch case e => None

  /** Search for the given object in all the loaded jars */
  private def findObject[T](objName: String)(using ClassTag[T]): Option[T] =
    def getObj = getObject[T](objName)
    for loader <- classLoaders do
      getObj(loader) match
        case Some(v): Some[T] => return Some(v)
        case _                => ()
    logger.error(
      s"Plugin $objName not found inside module com.anglypascal.scalite.plugins"
    )
    None

  /** Find the objects of type T in the jars */
  private def findObjects[T](names: List[String])(using ClassTag[T]) =
    names.map(findObject[T]).filter(_ == None).map(_.get)

  private def loadConverters(names: List[String]) =
    findObjects[Converter](names).map(Converters.addConverter)

  private def loadCollections(names: List[String]) =
    findObjects[Collection[?]](names).map(Collections.addToCollection)

  private def loadLayouts(names: List[String]) =
    findObjects[LayoutObject](names).map(Layouts.addEngine)

  private var classLoaders = List[URLClassLoader]()

  /** Create classLoaders from all the jar files in the pluginsDir */
  private def getClassLoaders(pluginsDir: String): Unit =
    classLoaders = getListOfFiles(pluginsDir)
      .filter(_.endsWith(".jar"))
      .map(loadJar)

  def apply(pluginsDir: String, pluginsData: DObj): Unit =
    getClassLoaders(pluginsDir)

    def getArrStr(key: String): List[String] =
      if pluginsData.contains(key) then
        pluginsData(key) match
          case d: DArr =>
            d.filter(_.isInstanceOf[DStr]).map(_.asInstanceOf[DStr].str)
          case _ => List()
      else List()

    loadConverters(getArrStr("converters"))
    loadCollections(getArrStr("collections"))
    loadLayouts(getArrStr("layouts"))
