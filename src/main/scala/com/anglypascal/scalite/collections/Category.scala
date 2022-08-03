package com.anglypascal.scalite.collections

import com.rallyhealth.weejson.v1.Obj

/** Categories can be part of the post url, and also posts nested in a subfolder
  * structure will automatically have the folder names as categories
  */
class Category(name: String, globals: Obj)
    extends CollectionOfPosts("category", name, globals)
