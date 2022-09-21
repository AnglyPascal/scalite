package com.anglypascal.scalite

import com.anglypascal.scalite.data.mutable.DObj
import com.anglypascal.scalite.data.mutable.DArr

object Defaults:

  val title = "A Shiny New Website"
  val description = "Site description"
  val permalink = "{{> none }}"
  val staticFilesPermalink = "/{{title}}.{{outputExt}}"
  val dateFormat = "yyyy-MM-dd"
  val showExceprts = true
  val lang = "en"
  val paginate = false
  val author = DObj()
  val timeZone = "Europe/London"
  val separator = "\n\n"
  val replaceAssets = true

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
    val include = DArr(".htaccess")
    val exclude = DArr("build.sbt")
    val keepFiles = DArr(".git", ".svn") // give regex list
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
    val style = "post"

  object Drafts:
    val output = false
    val folder = "/_drafts"
    val name = "drafts"
    val directory = Directories.collectionsDir
    val sortBy = "dates"
    val toc = false
    val permalink = "/{{collection}}/{{> date}}"
    val layout = "draft"
    val style = "post"

  object Statics:
    val output = true
    val folder = "/_statics"
    val name = "statics"
    val directory = Directories.collectionsDir
    val sortBy = "dates"
    val toc = false
    val permalink = "/{{> none}}"
    val layout = "default"
    val style = "page"

  object Sass:
    val output = true
    val folder = "/_sass"
    val name = "sass"
    val directory = Directories.collectionsDir
    val sortBy = "title"
    val toc = false
    val layout = ""
    val permalink = "/{{title}}{{outputExt}}"
    val style = "page"
    val extensions = "sass,scss"
    val outputExt = ".css"
    val outputStyle = "compressed"

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

  object Tree:
    val permalink = "/{{ctype}}/{{name}}"
    val defaultStyle = "tag"
    val defaultType = "tags"

  object PostTree:
    val outputExt = ".html"
    val permalink = "/{{path}}"
    val sortBy = "title"

  object Tags:
    val title = "Tags"
    val tType = "tags"
    val sortBy = "title"
    val baseLink = "/{{gType}}"
    val relativeLink = "/{{name}}"
    val separator = List(" ", ",")
    val style = "tag"

  object Categories:
    val title = "Categories"
    val tType = "categories"
    val sortBy = "title"
    val baseLink = "/{{gType}}"
    val relativeLink = "/{{name}}"
    val separator = List(",")
    val style = "category"

  object Markdown:
    val extensions = "markdown,md,mkd,mkdn"
    val outputExt = ".html"

  object Identity:
    val extensions = "html"
    val outputExt = ".html"
