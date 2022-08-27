package com.anglypascal.scalite

import com.anglypascal.scalite.commands.DryRun
import com.anglypascal.scalite.utils.yamlParser
import scala.io.Source

@main
def main = 
  // val readmeText : Iterator[String] = Source.fromResource("site_template/about.md").getLines
  val dr = DryRun
  val dir = "/home/ahsan/git/scalite/src/main/resources/site_template"
  dr.run("/home/ahsan/git/scalite/src/main/resources/site_template")
  // val conf = yamlParser(dir + "/_config.yml")
  // println(conf)

