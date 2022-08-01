package com.anglypascal.scalite.readers

import com.anglypascal.scalite.Layout

class PartialsReader(directory: String) extends LayoutsReader(directory)

object PartialsReader:
  def apply(directory: String) = (new PartialsReader(directory)).getObjectMap


