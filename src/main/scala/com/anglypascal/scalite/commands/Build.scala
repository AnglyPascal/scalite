package com.anglypascal.scalite.commands

import com.anglypascal.scalite.Globals
import com.anglypascal.scalite.collections.Collections

object Build extends Command:

  def build(sitePath: String): Unit =

    val globals = Globals(sitePath)

    /** Where should posts go?
     *
     *  this is determined by their url structure. The same hierarchy works
     */

    /** things:
      *
      *   - Clean the build site
      *   - process all collecitons
      *   - process all groups
      *   - compile _sass
      *   - move static files
      */
