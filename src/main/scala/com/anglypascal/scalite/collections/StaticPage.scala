package com.anglypascal.scalite.collections

import com.anglypascal.scalite.data.DObj

class StaticPage(
    parentDir: String,
    relativePath: String,
    globals: DObj,
    colName: String
) extends Post(parentDir, relativePath, globals, colName) // except for the date
