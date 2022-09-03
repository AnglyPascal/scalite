package com.anglypascal.scalite.converters

import com.typesafe.scalalogging.Logger

class Identity(
    val fileType: String,
    val extensions: String,
    val outputExt: String
) extends Converter:

  private val logger = Logger("Identity converter")

  def convert(str: String, filepath: String): String =
    logger.debug(s"Converting $filepath using the identity converter")
    str

object Identity extends ConverterConstructor:
  val constructorName: String = "identity"
  def apply(fileType: String, extensions: String, outputExt: String) =
    new Identity(fileType, extensions, outputExt)
