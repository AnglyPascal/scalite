package com.anglypascal.scalite.commands

import com.anglypascal.scalite.Globals
import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.utils.Cleaner

object Build extends Command:

  def run(): Unit =
    run(System.getProperty("user.dir"))

  def run(sitePath: String): Unit =
    // Get the global configs
    val globals = Globals(sitePath)
    // Clean the build site
    Cleaner(globals)
    Collections.process()

    /** Where should posts go?
      *
      * this is determined by their url structure. The same hierarchy works
      */

    /** things:
      *   - process all groups
      *   - compile _sass
      *   - move static files
      */
