package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj

class StaticPage(
    parentDir: String,
    relativePath: String,
    globals: DObj
) extends Post(parentDir, relativePath, globals) // except for the date
