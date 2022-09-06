package com.anglypascal.scalite.documents

import com.anglypascal.scalite.plugins.Plugin
import scala.collection.mutable.ListBuffer

trait Generator extends Plugin:

  def pages: List[Page]

  def process(dryRun: Boolean = false): Unit
