package com.anglypascal.scalite.commands

import com.anglypascal.scalite.initialize
import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.utils.Cleaner
import com.anglypascal.scalite.groups.Clusters

object Build extends Command:

  def run(): Unit =
    run(System.getProperty("user.dir"))

  def run(sitePath: String): Unit =
    // Get the global configs
    val globals = initialize(sitePath)
    // Clean the build site
    Cleaner(globals)
    Collections.process()
    Clusters.process(false)

    /** Where should posts go?
      *
      * this is determined by their url structure. The same hierarchy works
      */

    /** things:
      *   - compile _sass
      *   - move static files
      */
