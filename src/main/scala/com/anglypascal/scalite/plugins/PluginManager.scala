package com.anglypascal.scalite.plugins

import com.anglypascal.scalite
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import scala.reflect.ClassTag

import scalite.collections.Collections
import scalite.collections.ElemConstructor
import scalite.converters.ConverterConstructor
import scalite.converters.Converters
import scalite.layouts.LayoutGroupConstructor
import scalite.layouts.Layouts
import scalite.trees.Forest
import scalite.trees.Forests
import scalite.trees.PostForests
import scalite.trees.TreeStyle
import scalite.trees.AnyStylePost

/** Load Plugin objects form the jar files in the plugins directory */
object PluginManager:

  private val logger = Logger("Plugin Manager")

  def loadJar(pathToJar: String)(using man: ClassTag[Plugin]): Unit =
    logger.debug(s"loading jar file $pathToJar")

    val jar = new JarFile(pathToJar)
    val e = jar.entries()

    object P extends Plugin

    val urls = Array(new URL("jar:file:" + pathToJar + "!/"))
    val cl = URLClassLoader.newInstance(urls, P.getClass.getClassLoader);

    while e.hasMoreElements do
      val je = e.nextElement

      if (!je.isDirectory && je.getName.endsWith("$.class")) then
        val className =
          je.getName.substring(0, je.getName.length - 6).replace('/', '.')
        val c = cl.loadClass(className)

        if !c.getFields.filter(_.getName == "MODULE$").isEmpty then
          c.getField("MODULE$").get(man.runtimeClass) match
            case c: ConverterConstructor =>
              logger.debug(
                s"loading ${GREEN("ConverterConstructor")} ${BLUE(className)}"
              )
              Converters.addConverterConstructor(c)
            case h: Hook =>
              logger.debug(s"loading ${GREEN("Hook")} ${BLUE(className)}")
              Hooks.registerHook(h)
            case e: ElemConstructor =>
              logger.debug(
                s"loading ${GREEN("ElemConstructor")} ${BLUE(className)}"
              )
              Collections.addStyle(e)
            case t: AnyStylePost =>
              logger.debug(
                s"loading ${GREEN("ElemConstructor")} ${BLUE(className)}"
              )
              PostForests.addTreeStyle(t)
            case f: Forest[?] =>
              logger.debug(s"loading ${GREEN("Forest")} ${BLUE(className)}")
              Forests.addForest(f)
            case l: LayoutGroupConstructor =>
              logger.debug(
                s"loading ${GREEN("LayoutGroupConstructor")} ${BLUE(className)}"
              )
              Layouts.addEngine(l)
            case _ =>

  def apply(pluginsDir: String, pluginsData: DArr): Unit =

    val plugins = pluginsData.flatMap(_.getStr).toList

    val files =
      import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
      getListOfFilepaths(pluginsDir)
        .filter(f =>
          f.endsWith(".jar") &&
            plugins.foldLeft(false)(_ || f.contains(_))
        )

    files foreach { loadJar }
