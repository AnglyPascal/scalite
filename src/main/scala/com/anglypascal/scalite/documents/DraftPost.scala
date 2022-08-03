package com.anglypascal.scalite.documents

import com.rallyhealth.weejson.v1.Obj

class DraftPost(filename: String, layouts: Map[String, Layout], globals: Obj)
    extends Post(filename, layouts, globals) // except for the date

/** TODO: Draft posts in _draft folder. These will be rendered in the drafts:
  * true option is set in the global settings. The time variable for these will
  * be the motified date collected from the file informations.
  */
