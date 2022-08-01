// package com.anglypascal.scalite

import com.anglypascal.mustache.Mustache 

import java.nio.file.{Files, Path}
import com.rallyhealth.weejson.v1.yaml.FromYaml
import com.rallyhealth.weejson.v1._

import sttp.client3._

/** Class to read a markdown file and separate out the yaml content from the markdown
 *  Provides methods to access the markdown, html, and the yaml data from the file
 */

abstract class Reader(val filename: String) {
  private val raw = scala.io.Source.fromFile(filename).mkString
  private val yaml_regex = raw"\A---\n?([\s\S\n]*)---\n?([\s\S\n]*)".r

  protected var yaml = ""
  protected var text = ""

  raw match {
    case yaml_regex(a, b) => {yaml = a; text = b}
    case _ => { text = raw }
  }

  val data: Value = yaml match {
    case "" => Obj()
    case s =>  FromYaml(s).transform(Value)
  }
}

class MarkdownReader(filename: String) extends Reader(filename){
  def toHTML: String = {
    val backend = HttpClientSyncBackend()
    val response = basicRequest
      .body(text)  
      .post(uri"https://api.github.com/markdown/raw")
      .header("Content-Type", "text/plain")
      .header("Charset", "UTF-8")
      .send(backend).body 

    response match {
      case Left(_) => ""
      case Right(s) => s
    }
  }
}

class HtmlReader(filename: String) extends Reader(filename){
  def toHTML: String = text
}


object Reader {
  def main(args : Array[String]) : Unit = {
    val r = new Post("about.md")
    println(r.render())
    // val l = new Layout("default.html")
  }
}
