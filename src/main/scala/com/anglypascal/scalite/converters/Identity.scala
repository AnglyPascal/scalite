package com.anglypascal.scalite.converters

import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.data.immutable.DObj
import com.anglypascal.scalite.Defaults

class Identity(
    protected val configs: DObj,
    protected val globals: DObj
) extends Converter:

  def fileType: String = configs.getOrElse("fileType")("none")

  def extensions: String =
    configs.getOrElse("extensions")(Defaults.Identity.extensions)

  def outputExt: String = 
    configs.getOrElse("outputExt")(Defaults.Identity.outputExt)

  private val logger = Logger("Identity converter")

  def convert(str: String, filepath: String): String =
    logger.debug(s"Converting $filepath using the identity converter")
    str

object Identity extends ConverterConstructor:
  val constructorName: String = "identity"
  def apply(configs: DObj, globals: DObj) =
    new Identity(configs, globals)
