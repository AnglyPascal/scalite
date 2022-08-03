package com.anglypascal.scalite.collections

import com.rallyhealth.weejson.v1.Obj

/** Tag are less "powerful" than categories, and can't be part of the post url
  */
class Tag(name: String, globals: Obj)
    extends CollectionOfPosts("tag", name, globals)
