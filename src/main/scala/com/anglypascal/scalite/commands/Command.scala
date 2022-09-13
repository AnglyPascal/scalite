package com.anglypascal.scalite.commands

import com.anglypascal.scalite.Site

trait Command:
  def run(site: Site): Unit
