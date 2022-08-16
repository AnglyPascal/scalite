package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.data.Data

/** Simple trait to allow for plugin creation and loading. Exists solely to tag
  * an object as Plugin
  */
trait Plugin:

  /** Meant to be overriden by custom plugins to provide configurability through
    * the global configs file
    */
  def addConfigs(data: Data): Plugin = this
