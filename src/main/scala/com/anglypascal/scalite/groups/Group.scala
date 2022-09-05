package com.anglypascal.scalite.groups

import com.anglypascal.scalite.documents.Renderable
import scala.collection.mutable.LinkedHashMap

trait Group[+A <: Renderable] extends Renderable:

  val items: LinkedHashMap[String, A]

  val name: String

trait SuperGroup[+A <: Renderable] extends Group[Group[A]] with Renderable
