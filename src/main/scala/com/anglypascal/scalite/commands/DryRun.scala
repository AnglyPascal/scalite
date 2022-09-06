package com.anglypascal.scalite.commands

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.documents.Assets
import com.anglypascal.scalite.groups.Clusters
import com.anglypascal.scalite.utils.Cleaner
import com.anglypascal.scalite.Site

object DryRun extends Command:

  def run(): Unit =
    run(System.getProperty("user.dir"))

  def run(sitePath: String): Unit =

    val globals = Site(sitePath)

    Collections.process(true)
    Clusters.process(true)
    Assets.copy()

