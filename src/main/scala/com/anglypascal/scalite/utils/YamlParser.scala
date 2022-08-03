package com.anglypascal.scalite.utils

import com.anglypascal.scalite.YAMLParserException

import _root_.com.rallyhealth.weejson.v1.yaml.FromYaml
import _root_.com.rallyhealth.weejson.v1._

def yamlParser: String => Obj =
  FromYaml(_).transform(Value) match
    case v: Obj => v
    case _ => throw YAMLParserException("YAML couldn't be parsed into a map.")
