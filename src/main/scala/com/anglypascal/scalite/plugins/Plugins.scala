package com.anglypascal.scalite.plugins

// import com.anglypascal.scalite.documents.MustacheLayout
// import com.anglypascal.scalite.collections.Posts
// import com.anglypascal.scalite.converters.Markdown
import scala.reflect.ClassTag

import scala.collection.mutable.Set
// import com.typesafe.scalalogging.Logger

trait PluginsFactory:
  def addPlugin(plugin: Plugin): Unit

/** Current idea is that:
  *   - a classpath containing all the plugins will be passed to the compiler
  *   - will have to recompile to add those plugins unfortunately.
  *   - will add the names of the plugins in the module.PluginName format
  *   - and they will be added to the compiler
  */
object Plugins extends PluginsFactory:

  // val listOfPlubins = Set[Plugin](Posts, Markdown, MustacheLayout)
  val listOfPlubins = Set[Plugin]()
  listOfPlubins.map(_.init)

  def addPlugin(plugin: Plugin) = listOfPlubins += plugin

  def addPlugin(pluginName: String) =
    try companion[Plugin](pluginName).init
    catch
      case e: ClassNotFoundException =>
        // Logger("Plugin").error(
        //   "couldn't find plugin " +
        //     s"com.anglypascal.scalite.$pluginName" + "$"
        // )
      case e =>
        // Logger("Plugin").error(
        //   s"${e.getMessage} was thrown while processing plugin " +
        //     s"com.anglypascal.scalite.$pluginName" + "$"
        // )

  def addPlugins(pnames: String*) = pnames.map(p => addPlugin(p))

  private def companion[T](name: String)(using man: ClassTag[T]): T =
    Class
      .forName("com.anglypascal.scalite." + name + "$")
      .getField("MODULE$")
      .get(man.runtimeClass)
      .asInstanceOf[T]
