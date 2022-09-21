package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.converters.ConverterConstructor
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.utils.DirectoryReader.getListOfFilepaths
import com.anglypascal.scalite.data.mutable.DArr
import com.anglypascal.scalite.data.mutable.DStr
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.trees.Forest
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.trees.Forests
import com.anglypascal.scalite.trees.PostForests
import com.anglypascal.scalite.trees.TreeStyle
import com.anglypascal.scalite.utils.Colors.*
import com.typesafe.scalalogging.Logger

import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile
import scala.reflect.ClassTag

/** Load Plugin objects form the jar files in the plugins directory */
object PluginManager:

  private val logger = Logger("PluginManager")

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
            // case c: TreeStyle[?] =>
            //   Forests.add
            case _ =>

  def apply(pluginsDir: String, pluginsData: DArr)(using
      man: ClassTag[Plugin]
  ): Unit =

    val plugins = pluginsData.flatMap(_.getStr).toList

    val files =
      getListOfFilepaths(pluginsDir)
        .filter(f =>
          f.endsWith(".jar") && plugins.foldLeft(false)(_ || f.contains(_))
        )

    files foreach { loadJar }
