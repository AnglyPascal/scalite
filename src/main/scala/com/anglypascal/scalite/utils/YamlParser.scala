package com.anglypascal.scalite.utils

import com.anglypascal.scalite.YAMLParserException

import _root_.com.rallyhealth.weejson.v1.yaml.FromYaml
import _root_.com.rallyhealth.weejson.v1._

def yamlParser(path: String): Obj = 
  val str = readFile(path).toString
  FromYaml(str).transform(Value) match
    case v: Obj => v
    case _ => throw YAMLParserException("YAML couldn't be parsed into a map.")
