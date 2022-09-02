package com.anglypascal.scalite

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

trait Configurable:

  val sectionName: String

  def apply(configs: MObj, globals: IObj): Unit

