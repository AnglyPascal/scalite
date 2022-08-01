package com.anglypascal.scalite

import scala.io.Source

def readFile(filename: String): Source = Source.fromFile(filename)

import _root_.com.rallyhealth.weejson.v1.yaml.FromYaml
import _root_.com.rallyhealth.weejson.v1._

def yamlParser: String => Value = FromYaml(_).transform(Value)

def titleParser(fn: String): String = ???

def dateParser(fn: String): String = ???
