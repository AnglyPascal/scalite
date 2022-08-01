package com.anglypascal.scalite

/** Exception trait to throw Scalite specific exceptions
  *
  * TODO: need to add more functionality, to give more information
  */
sealed trait ScaliteException(log: String) extends Exception

/** To be thrown when the converter fails to convert the content */
case class ConverterException(log: String) extends ScaliteException(log)

/** To be thrown when no layout is specified for a page or when the parent
  * layout can't be found
  */
case class NoLayoutException(log: String) extends ScaliteException(log)

/** To be thrown when the Mustache template fails to render */
case class LayoutRenderingException(log: String) extends ScaliteException(log)

/** To be thrown when the YAML parser fails or can't parse the YAML to a weejson
  * Obj or a Map
  */
case class YAMLParserException(log: String) extends ScaliteException(log)
