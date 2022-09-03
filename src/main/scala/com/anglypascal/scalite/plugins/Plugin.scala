package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.data.mutable.DObj

/** Trait to allow for plugin creation. Plugins to be loaded at runtime should
  * extend this trait.
  */
trait Plugin:

  /** May be overriden to accept configuration through \_config.yml */
  protected[plugins] def addConfigs(data: DObj): Plugin = this
