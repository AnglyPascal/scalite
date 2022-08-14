package com.anglypascal.scalite.plugins

trait Plugin:
  def init: Unit =
    println("hello")
    Plugins.addPlugin(this)

