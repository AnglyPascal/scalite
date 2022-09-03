package com.anglypascal.scalite

import com.anglypascal.scalite.collections.*
import com.anglypascal.scalite.documents.*

/** the main thing, anchor of this project
  */

/** TODO: Caching. modified date is collected from the file itself somehow and
  * used if the file has no date or is in _draft folder the cache structure can
  * be inspired from https://jekyllrb.com/docs/structure/
  */
case class Site(base_dir: String)

  /** */
  // val globals = Globals.globals

  // val layouts = MustacheLayout(globals("base").str + globals("layout_dir").str)
  // val partials = Partial(globals("base").str + globals("includes_dir").str)
  // val statics = Post(globals("base").str, globals)
  // val posts = Post(globals("base").str + globals("layout_dir").str, globals)

  /** site here controls the scoped defaults in config.yml.
    */

/** Learn to control log level
  */
