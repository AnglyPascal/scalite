package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.converters.Converter

import java.io.File
import java.net.{URL, URLClassLoader}

trait SomeTrait:
  def someMethod: String;

object SomeObject extends SomeTrait:
  def someMethod = "something";

object Plug extends Plugin

@main
def pluginTest =
  val jarPathName = "/home/ahsan/git/scalite/PP.jar"
  object P extends Plugin
  val classLoader = new URLClassLoader(
    Array[URL](new File(jarPathName).toURI.toURL),
    P.getClass.getClassLoader
  )
  val classToLoad = classLoader.loadClass("com.anglypascal.scalite.plugins.PP")
  classToLoad.getDeclaredConstructor().newInstance().asInstanceOf[Plugin].init

  Plugins.listOfPlubins.map(_.getClass.getSimpleName).map(println(_))
