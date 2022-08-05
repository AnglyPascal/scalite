package com.anglypascal.scalite.utils

/** TODO: need to make it more general, check Jekyll
  */
def slugify(
    str: String,
    modes: String = "default",
    cased: Boolean = false
): String =
  str.toLowerCase.replace(' ', '-')
