package com.anglypascal.scalite.converters

import com.typesafe.scalalogging.Logger

object Identity extends Converter:

  val fileType = "all"

  private val logger = Logger("Markdown converter")

  setExt(".*")

  def outputExt = ".html"

  def convert(str: String, filepath: String): String =
    logger.debug(s"Converting $filepath using the identity converter")
    str
