package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.converters.Converter
import com.anglypascal.scalite.converters.Converters

import java.io.File
import java.net.URL
import java.net.URLClassLoader

trait SomeTrait:
  def someMethod: String;

object SomeObject extends SomeTrait:
  def someMethod = "something";

object Plug extends Plugin

// @main
def pluginTest =
  val jarPathName = "/home/ahsan/git/scalite/PP.jar"
  object P extends Plugin
  val classLoader = new URLClassLoader(
    Array[URL](new File(jarPathName).toURI.toURL),
    P.getClass.getClassLoader
  )
  val classToLoad =
    classLoader.loadClass("com.anglypascal.scalite.plugins.PPConverter$")
  val a = classToLoad.getField("MODULE$").get(null).asInstanceOf[Converter]
  Converters.hasConverter("haha.pp")

  // Plugins.listOfPlubins.map(_.getClass.getSimpleName).map(println(_))
