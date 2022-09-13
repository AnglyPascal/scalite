package com.anglypascal.scalite

import com.anglypascal.scalite.utils.StringProcessors.quote
import com.anglypascal.scalite.converters.Converters
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.layouts.Layouts
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.groups.PostCluster
import com.anglypascal.scalite.collections.PageLike
import com.anglypascal.scalite.collections.Excerpt

@main
def main(args: String*) = 
  val usage = """ """
  
  var command = "build"
  var directory = System.getProperty("user.dir")


  /** do some parsing */
