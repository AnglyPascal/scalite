package com.anglypascal.scalite.converters

import com.anglypascal.scalite.{Converter, ConverterException}

val converters = Array[Converter](Markdown)

def findConverter(ext: String): Converter =
  converters.filter(_.matches(ext)).head

def convert(str: String, filename: String): Either[ConverterException, String] =
  val converter = findConverter(filename)
  converter.convert(str)
