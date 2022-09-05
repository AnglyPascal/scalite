package com.anglypascal.scalite

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

/** Objects that can be configured by the \_config.yml file
  *
  * Objects that implement Configurable will look for the section named
  * `sectionName` in the configs file, and configure itself with the
  * configurations.
  */
trait Configurable:

  /** Section under where the configs of this Configurable is */
  val sectionName: String

  /** Apply the configurations */
  def apply(configs: MObj, globals: IObj): Unit
