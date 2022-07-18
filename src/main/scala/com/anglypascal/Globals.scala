package com.anglypascal.scalite

import com.rallyhealth.weejson.v1._
import com.rallyhealth.weejson.v1.yaml.{FromYaml, ToYaml}

import java.io.File
import scala.collection.mutable.LinkedHashMap

object Globals {
  val siteDir = "/home/ahsan/git/scalite/src/main/scala/com/anglypasal/site_template"
  val postsDir = "/_posts"
  val layoutsDir = "/_layouts"
  val includesDir = "/_includes"
  val baseURL = "/"
  val error = siteDir + "/404.html"

  val siteObj = Obj(
    "title" -> "blank title",
    "lang"  -> "en",
    "paginate" -> Bool(false),
    "show_excerpts" -> Bool(true),
    "root_url" -> "/",
    "author" -> Obj(),
    "description" -> "blank description"
  )
  val layouts = LinkedHashMap[String, Layout]()

  readConfig()
  readLayouts()

  val partials = layouts.filter(p => List("head", "header", "footer").contains(p._1))

  private def readConfig(): Unit = {
    val yaml = scala.io.Source.fromFile(siteDir + "/config.yml").mkString
    val data: Value = FromYaml(yaml).transform(Value)
    for (key <- data.obj.keys)
      siteObj(key) = data(key)
  }

  private def readLayouts(): Unit = {
    def fileToLayout(file: File): (String, Layout) = {
      val ln = file.getPath().split("/").last.split('.').head
      (ln, new Layout(ln))
    }
    
    layouts ++= 
      List(layoutsDir, includesDir)
        .map(x => new File(siteDir + x))
        .filter(d => d.exists && d.isDirectory)
        .map(_.listFiles.filter(_.isFile).toList)
        .flatten
        .map(fileToLayout)
  }

  private def readHeaderFooter(): Unit = {
  }

  def findLayout(filename: String): Option[String] = {
    val f1 = new File(siteDir + layoutsDir + "/" + filename + ".html")
    val f2 = new File(siteDir + includesDir + "/" + filename + ".html")

    if (f1.exists) Some(f1.getPath)
    else if (f2.exists) Some(f2.getPath)
    else None
  }
}
