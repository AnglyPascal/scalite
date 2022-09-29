package com.anglypascal.scalite.documents

import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.hooks.PageHooks
import com.anglypascal.scalite.utils.DirectoryReader.writeTo
import com.anglypascal.scalite.data.immutable.DObj
import scala.collection.mutable.LinkedHashMap

class PageClass(
    val id: String,
    val permalink: String,
    visible: Boolean,
    outputExt: String,
    val content: String,
    val locals: DObj,
    globals: DObj
):

  private val logger = Logger("Page")

  if visible then PageClass.addPage(this)

  def write(dryRun: Boolean = false): Unit =
    if !visible then return
    val path =
      if permalink.endsWith(outputExt) then permalink
      else permalink + outputExt

    if !dryRun then
      logger.debug(s"writing $this to $path")

      /** FIXME useless, think about what to do with this */
      PageHooks.beforeRenders(globals)(locals)
      val r = PageHooks.afterRenders(globals)(locals, content)
      writeTo(path, r)
      // FIXME PageHooks.afterWrites(globals)(this)
    else logger.debug(s"would write $this to $path")

object PageClass:

  private val pages = LinkedHashMap[String, PageClass]()

  def addPage(page: PageClass): Unit = pages += page.id -> page
