package com.anglypascal.scalite.trees

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Renderable
import org.scalatest.flatspec.AnyFlatSpec

class TreeSpecs extends AnyFlatSpec:

  class Item(val getPaths: Iterable[List[String]]) extends Renderable:
    protected lazy val layoutName: String = "empty"
    lazy val locals: IObj = IObj()
    protected val globals = IObj()
    val visible: Boolean = true
    protected def render(up: IObj = IObj()): String = ""

  class AnyTree(
      val treeName: String,
      val treeType: String,
      val parent: Option[Tree[Item]]
  )(val configs: MObj = MObj(), val globals: IObj = IObj())
      extends Tree[Item]:

    def createChild(name: String): Tree[Item] =
      new AnyTree(name, treeType, Some(this))(configs, globals)

    def getPaths(item: Item): Iterable[List[String]] =
      item.getPaths

    protected lazy val layoutName: String = "empty"
    lazy val locals: IObj = IObj()
    val visible: Boolean = true
    protected def render(up: IObj = IObj()): String = ""

    protected[trees] def process(dryRun: Boolean): Unit = ()

  val root = AnyTree("root", "anyTree", None)()
  val items = Map(
    "1" -> Item(List(List("a1", "b1"), List("a2"), List("a3", "b3", "c3"))),
    "2" -> Item(
      List(List("a1", "b1", "c1"), List("a2", "b2"), List("a3", "b3"))
    ),
    "3" -> Item(List(List("a1"), List("a2", "b2"), List("a3", "b3", "c3")))
  )

  it should "add items properly" in {
    items.map((k, v) => root.addItem(k, v))
    assert(
      root.children.length === 3 &&
        root.items.length === 0
    )
  }
