package com.anglypascal.scalite.utils

import com.anglypascal.scalite.data.mutable.DNull
import com.anglypascal.scalite.data.mutable.DObj
import com.anglypascal.scalite.data.mutable.Data
import com.anglypascal.scalite.data.mutable.DataImplicits
import com.anglypascal.scalite.utils.StringProcessors.quote
import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Value
import com.rallyhealth.weejson.v1.yaml.FromYaml
import com.typesafe.scalalogging.Logger

def yamlFileParser(path: String): Data =
  val logger = Logger("YAML File Parser")
  val str = DirectoryReader.readFile(path)
  try 
    val value = FromYaml(str).transform(Value)
    DataImplicits.fromValue(value)
  catch
    case e =>
      logger.error(s"${e.toString} occurred while parsing yaml file $path")
      DNull

def frontMatterParser(yaml: String): DObj =
  val logger = Logger("Front Matter Parser")
  try
    FromYaml(yaml).transform(Value) match
      case v: Obj => DObj(v)
      case some =>
        logger.warn("yaml string could not be parsed into an weejson Obj")
        logger.trace(
          s"yaml string could not be parsed into an weejson Obj: $yaml"
        )
        DObj()
  catch
    case e: com.rallyhealth.weepickle.v1.core.TransformException =>
      logger.error("failed to parse yaml string: " + quote(yaml))
      DObj()
    case e =>
      logger.error(e.getMessage())
      DObj()
