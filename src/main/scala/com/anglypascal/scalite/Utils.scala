package com.anglypascal.scalite

import scala.io.Source

def readFile(filename: String): Source = Source.fromFile(filename)

import _root_.com.rallyhealth.weejson.v1.yaml.FromYaml
import _root_.com.rallyhealth.weejson.v1._

def yamlParser: String => Obj = 
  FromYaml(_).transform(Value) match
    case v: Obj => v
    case _ => throw YAMLParserException("YAML couldn't be parsed into a map.")

def titleParser(fn: String): String = ???

def dateParser(fn: String): String = ???

extension (o: Obj)
  def contains(key: String) = o.obj.contains(key)
