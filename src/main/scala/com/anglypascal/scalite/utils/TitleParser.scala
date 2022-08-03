package com.anglypascal.scalite.utils

def titleParser(fn: String): Option[String] = 
  val title = raw"\d{4}-\d{2}-\d{2}(.*)".r
  fn match 
    case title(t) => Some(processTitle(t))
    case _ => None

def processTitle(fn: String): String = ???
