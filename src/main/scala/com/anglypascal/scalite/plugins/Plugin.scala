package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.data.mutable.{DObj => MObj}

/** Trait to allow for plugin creation. Plugins to be loaded at runtime should
  * extend this trait.
  */
trait Plugin:

  protected[plugins] def addConfigs(conf: MObj): Plugin = this

  override def toString(): String
