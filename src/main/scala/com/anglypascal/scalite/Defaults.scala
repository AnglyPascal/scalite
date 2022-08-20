package com.anglypascal.scalite

import com.rallyhealth.weejson.v1.Obj

object Defaults:
  val title = "A Shiny New Website"
  val description = "Site description"
  val permalinkTemplate = "{{> default }}"
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

  object Collection:
    val toc = false
    val sortBy = "title"

  object Posts:
    val output = true
    val name = "posts"
