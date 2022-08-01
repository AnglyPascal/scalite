package com.anglypascal.scalite

import scala.collection.mutable.Set
import com.rallyhealth.weejson.v1.Obj

trait Collection[A]:

  val name: String

  val things: Set[A]

  def render(context: Obj, partials: Map[String, Layout]): String 

  def add(a: A) = things += a

