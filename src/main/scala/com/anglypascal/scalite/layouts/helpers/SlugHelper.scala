package com.anglypascal.scalite.layouts.helpers

import com.anglypascal.mustache.MustacheHelperSupport
import com.anglypascal.scalite.utils.StringProcessors.*

trait SlugHelper:
  this: SlugHelper with MustacheHelperSupport =>

  import com.anglypascal.mustache.Extensions.findKey

  def slug(str: String, ren: (String => String)): String =
    slugify(ren(str))
