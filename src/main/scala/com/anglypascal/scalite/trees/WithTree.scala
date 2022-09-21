package com.anglypascal.scalite.trees

import com.anglypascal.scalite.documents.Renderable
import com.anglypascal.scalite.data.mutable.Data
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import com.anglypascal.scalite.data.mutable.DObj

trait WithTree[T <: Renderable]:
  this: Renderable =>

  /** The map holding sets of collection-types */
  private val _trees = LinkedHashMap[String, ListBuffer[Tree[T]]]()

  def trees = _trees.map(p => (p._1, p._2.toList)).toMap

  def treeObj: DObj =
    val obj = DObj()
    for (k, s) <- _trees do obj += k -> s.map(_.locals).toArray
    obj

  def getTreesList(treeType: String): Data

  /** Adds the collection in the set of this collection-type */
  def addTree[A <: Tree[T]](treeType: String)(a: A): Unit =
    if _trees.contains(treeType) then _trees(treeType) += a
    else _trees += treeType -> ListBuffer(a)
