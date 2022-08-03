package com.anglypascal.scalite.readers

import com.anglypascal.scalite.documents.Layout

/** Read the layouts in the _includes directory and mark them as partials
  */
class PartialsReader(directory: String) extends LayoutsReader(directory)

object PartialsReader:
  def apply(directory: String) = (new PartialsReader(directory)).getObjectMap
