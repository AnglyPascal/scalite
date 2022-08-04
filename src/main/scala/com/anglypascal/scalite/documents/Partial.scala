package com.anglypascal.scalite.documents

import com.anglypascal.scalite.utils.{getListOfFiles, getFileName}

object Partial:

  private var _partials: Map[String, Layout] = _
  def partials = _partials

  def apply(directory: String): Map[String, Layout] =
    val files = getListOfFiles(directory)
    val ls = files
      .map(f => {
        val fn = getFileName(f)
        (fn, new Layout(getFileName(f), f))
      })
      .toMap

    ls.map((s, l) => l.setParent(ls))
    _partials = ls.toMap
    partials
