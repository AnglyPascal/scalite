package com.anglypascal.scalite.commands

import com.anglypascal.scalite.collections.Collections
import com.anglypascal.scalite.documents.Assets
import com.anglypascal.scalite.groups.Clusters
import com.anglypascal.scalite.utils.Cleaner
import com.anglypascal.scalite.Site

object DryRun extends Command:

  def run(site: Site): Unit =
    run(System.getProperty("user.dir"), site)

  private def run(sitePath: String, site: Site): Unit =

    val site = Site(sitePath)

    Collections.process()
    Clusters.process()
    Assets.copy()

    /** things:
      *   - compile _sass
      */
