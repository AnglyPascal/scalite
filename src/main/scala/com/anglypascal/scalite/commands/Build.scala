package com.anglypascal.scalite.commands

import com.anglypascal.scalite.Site

object Build extends Command:

  def run(site: Site): Unit =
    site.build()
