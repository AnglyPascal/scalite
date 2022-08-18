package com.anglypascal.scalite

import com.rallyhealth.weejson.v1.Arr
import com.rallyhealth.weejson.v1.Obj

object GlobalDefaults:

  object Dirs:
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

  object Site:
    val title = "A Shiny New Website"
    val lang = "en"
    val rootUrl = "/"
    val description = "generic site description"
    val author = Obj()
    val paginate = false
    val showExcerpts = true
    val dateFormat = "dd MMM, yyyy"
    val urlTemplate = "{{default}}"

  object Posts:
    val output = true
    val folder = "/_posts"
    val directory = Dirs.collectionsDir
    val sortBy = "dates"
    val toc = false

  object Drafts:
    val output = false
    val folder = "/_drafts"
    val directory = Dirs.collectionsDir
    val sortBy = "dates"
    val toc = false

  object Build:
    val logLevel = 1

