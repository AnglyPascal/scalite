package com.anglypascal.scalite.converters

import com.anglypascal.scalite.{Converter, ConverterException}

val converters = Array[Converter](Markdown)

def findConverter(ext: String): Option[Converter] =
  converters.filter(_.matches(ext)).headOption

def hasConverter(ext: String): Boolean = 
  findConverter(ext) != None

def convert(str: String, filename: String): Either[ConverterException, String] =
  findConverter(filename) match 
    case Some(converter) => converter.convert(str)
    case None => Left(ConverterException("No converter defined for filetype"))
