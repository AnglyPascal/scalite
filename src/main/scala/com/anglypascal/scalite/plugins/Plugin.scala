package com.anglypascal.scalite.plugins

/** Simple trait to allow for plugin creation and loading. Exists solely to tag an
 *  object as Plugin
 */
trait Plugin:
  def init: Unit = ()

