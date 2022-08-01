package com.anglypascal.scalite

sealed trait ScaliteException(log: String) extends Exception

case class ConverterException(log: String) extends ScaliteException(log)

case class NoLayoutException(log: String) extends ScaliteException(log)

case class LayoutRenderingException(log: String) extends ScaliteException(log)

case class YAMLParserException(log: String) extends ScaliteException(log)
