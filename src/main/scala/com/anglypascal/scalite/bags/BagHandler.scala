package com.anglypascal.scalite.bags

import com.anglypascal.scalite.documents.Post

import scala.collection.mutable.LinkedHashMap
import com.rallyhealth.weejson.v1.Obj

trait BagHandler[A <: PostsBag]:

  val bags: LinkedHashMap[String, A]

  def addToBags(post: Post, globals: Obj): Unit

object BagHandler:

  val availableBags = List(Tag, Category)
