package com.anglypascal.scalite.documents

import com.anglypascal.scalite.converters.Converters

trait Convertible(filepath: String):

  private val converter = Converters.findByExt(filepath)

  def convert(content: String): String =
    converter match
      case Some(c) => c.convert(content, filepath)
      case None    => content
