package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.converters.Converter
// import com.anglypascal.scalite.data.immutable.Data
// import com.anglypascal.scalite.data.immutable.DObj

object PPConverter extends Converter with Plugin:

  val fileType = "pp"

  def extensions = ".pp"

  def outputExt: String = "fuck"

  def convert(str: String, filepath: String): String = str

  
  // def hello = println("this is pp speaking")
