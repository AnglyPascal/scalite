package com.anglypascal.scalite

import com.anglypascal.scalite.utils.StringProcessors.quote
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.trees.PostForests
import com.anglypascal.scalite.collections.PageLike
import com.anglypascal.scalite.collections.Excerpt
import com.anglypascal.scalite.plugins.PluginManager

@main
def main(args: String*) = 
  val file = "plugins/scaliteTextile/target/scala-3.2.0/scalitetextile_3-0.1.0-SNAPSHOT.jar"
  val cl = PluginManager.loadJar(file)
  // println(cl)
