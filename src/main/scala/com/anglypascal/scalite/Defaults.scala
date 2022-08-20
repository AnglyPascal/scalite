package com.anglypascal.scalite

import com.rallyhealth.weejson.v1.Obj
import com.rallyhealth.weejson.v1.Arr

object Defaults:

  val title = "A Shiny New Website"
  val description = "Site description"
  val permalinkTemplate = "{{> default }}"
  val staticFilesPermalink = "/{{title}}.{{outputExt}}"
  val dateFormat = "dd MMM, yyyy"
  val showExceprts = true
  val lang = "en"
  val paginate = false
  val author = Obj()

  object Directories:
    val base = "."
    val collectionsDir = ""
    val destination = "/_site"
    val layoutsDir = "/_layouts"
    val includesDir = "/_includes"
    val sassDir = "/_sass"
    val dataDir = "/_data"
    val pluginsDir = "/_plugins"

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
    val toc = false

  object Drafts:
    val output = false
    val folder = "/_drafts"
    val directory = Directories.collectionsDir
    val sortBy = "dates"
    val toc = false

  object Build:
    val logLevel = 1

