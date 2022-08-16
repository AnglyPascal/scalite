package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.plugins.Plugin
import com.anglypascal.scalite.converters.Converter
import com.anglypascal.scalite.data.Data
import com.anglypascal.scalite.data.DObj

object PPConverter extends Converter with Plugin:
  val fileType = "pp"
  setExt("pp,peepee")
  def outputExt: String = "fuck"
  def convert(str: String, filepath: String): String = str

  
  // def hello = println("this is pp speaking")
