package com.anglypascal.scalite.readers

import com.anglypascal.scalite.documents.Layout
import com.rallyhealth.weejson.v1.Obj

/** Read the layouts in the _includes directory and mark them as partials
  */
class PartialsReader(directory: String, globals: Obj)
    extends LayoutsReader(directory, globals)

object PartialsReader:
  def apply(directory: String, globals: Obj) =
    (new PartialsReader(directory, globals)).getObjectMap
