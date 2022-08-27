package com.anglypascal.scalite.utils

import com.anglypascal.scalite.YAMLParserException
import com.rallyhealth.weejson.v1._
import com.rallyhealth.weejson.v1.yaml.FromYaml
import com.typesafe.scalalogging.Logger

def yamlFileParser(path: String): Obj = 
  val logger = Logger("YAML File Parser")
  val str = DirectoryReader.readFile(path).getLines.mkString("\n")
  try
    FromYaml(str).transform(Value) match
      case v: Obj => v
      case some => 
        logger.warn(s"yaml file $path could not be read into an weejson Obj")
        Obj()
  catch
    case e => 
      logger.error(s"${e.toString} occurred while parsing yaml file $path")
      Obj()

def yamlParser(yaml: String): Obj = 
  val logger = Logger("YAML Parser")
  try
    FromYaml(yaml).transform(Value) match
      case v: Obj => v
      case some => 
        logger.warn("yaml string could not be parsed into an weejson Obj")
        logger.trace(s"yaml string could not be parsed into an weejson Obj: $yaml")
        Obj()
  catch
    case e => 
      logger.error(e.toString)
      Obj()
