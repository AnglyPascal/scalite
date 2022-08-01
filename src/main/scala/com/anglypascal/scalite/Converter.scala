package com.anglypascal.scalite

import scala.util.matching.Regex

trait Converter:

  def ext: Regex

  def matches(filename: String): Boolean =
    ext.matches(filename)

  def outputExt: String

  def convert(str: String): Either[ConverterException, String]
