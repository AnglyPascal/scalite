package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.data.DObj

/** Simple trait to allow for plugin creation and loading. Exists solely to tag
  * an object as Plugin
  */
trait Plugin:

  /** Meant to be overriden by custom plugins to provide configurability through
    * the global configs file
    *
    * TODO: What kind of configs are we talking about here?
    *
    */
  def addConfigs(data: DObj): Plugin = this
