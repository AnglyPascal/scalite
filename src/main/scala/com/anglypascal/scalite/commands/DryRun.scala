package com.anglypascal.scalite.commands

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.documents.Assets
import com.anglypascal.scalite.groups.Clusters
import com.anglypascal.scalite.initialize
import com.anglypascal.scalite.utils.Cleaner

object DryRun extends Command:

  def run(): Unit =
    run(System.getProperty("user.dir"))

  def run(sitePath: String): Unit =

    val globals = initialize(sitePath)
    Cleaner(globals)

    Collections.process(true)
    Clusters.process(true)
    Assets.copy()

