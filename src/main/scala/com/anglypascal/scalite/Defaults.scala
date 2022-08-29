package com.anglypascal.scalite

import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Arr

object Defaults:

  val title = "A Shiny New Website"
  val description = "Site description"
  val permalink = "{{> none }}"
  val staticFilesPermalink = "/{{title}}.{{outputExt}}"
  val dateFormat = "yyyy-MM-dd"
  val showExceprts = true
  val lang = "en"
  val paginate = false
  val author = Obj()

  object Directories:
    val base = "."
    val collectionsDir = ""
    val destination = "/_site"
    val layoutsDir = "/_layouts"
    val partialsDir = "/_partials"
    val sassDir = "/_sass"
    val dataDir = "/_data"
    val pluginsDir = "/_plugins"
    val assetsDir = "/_assets"

  object Reading:
    val include = Arr(".htaccess")
    val exclude = Arr("build.sbt")
    val keepFiles = Arr(".git", ".svn") // give regex list
    val markdownExt = "markdown,mkdown,mkdn,mkd,md"
    val textileExt = "textile"
    val encoding = "utf-8"

  object Collection:
    val toc = false
    val sortBy = "title"

  object Posts:
    val output = true
    val folder = "/_posts"
    val name = "posts"
    val directory = Directories.collectionsDir
    val sortBy = "dates"
    val toc = true
    val permalink = "/{{> slugDate}}"
    val layout = "post"

  object Drafts:
    val output = false
    val folder = "/_drafts"
    val name = "drafts"
    val directory = Directories.collectionsDir
    val sortBy = "dates"
    val toc = false
    val permalink = "/{{collection}}/{{> date}}"
    val layout = "draft"

  object Statics:
    val output = true
    val folder = "/_statics"
    val name = "statics"
    val directory = Directories.collectionsDir
    val sortBy = "dates"
    val toc = false
    val permalink = "/{{> none}}"
    val layout = "default"

  object Build:
    val logLevel = 1

  object URLPartials:
    val date =
      "{{collection}}/{{categories}}/" +
        "{{year}}/{{month}}/{{day}}/{{title}}{{output_ext}}"
    val slugDate =
      "{{collection}}/{{categories}}/" +
        "{{year}}/{{month}}/{{day}}/{{slugTitle}}{{output_ext}}"
    val pretty =
      "{{categories}}/{{year}}/{{month}}/{{day}}/{{title}}"
    val ordinal =
      "{{categories}}/{{year}}/{{y_day}}/{{title}}{{output_ext}}"
    val weekdate =
      "{{categories}}/{{year}}/W{{week}}/{{short_day}}/{{title}}{{output_ext}}"
    val none =
      "{{categories}}/{{slugTitle}}{{output_ext}}"
