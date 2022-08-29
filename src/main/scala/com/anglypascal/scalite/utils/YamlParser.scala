package com.anglypascal.scalite.utils

import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Value
import com.rallyhealth.weejson.v1.yaml.FromYaml
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.utils.StringProcessors.quote

def yamlFileParser(path: String): Value =
  val logger = Logger("YAML File Parser")
  val str = DirectoryReader.readFile(path)
  try FromYaml(str).transform(Value)
  catch
    case e =>
      logger.error(s"${e.toString} occurred while parsing yaml file $path")
      null

def frontMatterParser(yaml: String): Obj =
  val logger = Logger("Front Matter Parser")
  try
    FromYaml(yaml).transform(Value) match
      case v: Obj => v
      case some =>
        logger.warn("yaml string could not be parsed into an weejson Obj")
        logger.trace(
          s"yaml string could not be parsed into an weejson Obj: $yaml"
        )
        Obj()
  catch
    case e: com.rallyhealth.weepickle.v1.core.TransformException =>
      logger.error("failed to parse yaml string: " + quote(yaml))
      Obj()
    case e =>
      logger.error(e.getMessage())
      Obj()
