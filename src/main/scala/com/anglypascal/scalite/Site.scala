package com.anglypascal.scalite

import com.anglypascal.scalite.documents.*
import com.anglypascal.scalite.collections.*
import com.anglypascal.scalite.data.DataAST

/** the main thing, anchor of this project
  */

/** TODO: Caching. modified date is collected from the file itself somehow and
  * used if the file has no date or is in _draft folder the cache structure can
  * be inspired from https://jekyllrb.com/docs/structure/
  */

/** CRUCIAL TODO: need to check if the weejson support in Mustache works
  */

/** CRUCIAL TODO: need to make a wrapper for the global value to make it
  * immutable. Only supply contains and get functions
  */

case class Site(base_dir: String):

  /** */
  val c = DataAST
  val globals = Globals.globals

  // val layouts = MustacheLayout(globals("base").str + globals("layout_dir").str)
  // val partials = Partial(globals("base").str + globals("includes_dir").str)
  // val statics = Post(globals("base").str, globals)
  // val posts = Post(globals("base").str + globals("layout_dir").str, globals)

  /** site here controls the scoped defaults in config.yml.
    */

/** Learn to control log level
  */
