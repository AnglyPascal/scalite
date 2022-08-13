package com.anglypascal.scalite

import scala.collection.mutable.Set
import com.anglypascal.scalite.documents.MustacheLayout
import com.anglypascal.scalite.collections.Posts
import com.anglypascal.scalite.utils.DataAST

object Plugins:

  val _mustacheLayout = MustacheLayout
  val _post = Posts
  val _dataAST = DataAST
