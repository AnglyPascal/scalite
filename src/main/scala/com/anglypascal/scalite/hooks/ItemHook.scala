package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.collections.ItemLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger

////////////
// Item //
////////////
sealed trait ItemHook extends Hook:
  override def toString(): String = super.toString() + "-Item"

trait ItemBeforeInit extends ItemHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait ItemBeforeLocals extends ItemHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait ItemBeforeRender extends ItemHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait ItemAfterRender extends ItemHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

object ItemHooks:

  private val logger = Logger("ItemHooks")

  private val _beforeInits = ListBuffer[ItemBeforeInit]()
  private val _beforeLocals = ListBuffer[ItemBeforeLocals]()
  private val _beforeRenders = ListBuffer[ItemBeforeRender]()
  private val _afterRenders = ListBuffer[ItemAfterRender]()

  def registerHook(hook: ItemHook) =
    hook match
      case hook: ItemBeforeInit   => _beforeInits += hook
      case hook: ItemBeforeLocals => _beforeLocals += hook
      case hook: ItemBeforeRender => _beforeRenders += hook
      case hook: ItemAfterRender  => _afterRenders += hook
      case null                   => ()

  def beforeInits(globals: IObj)(configs: IObj) =
    _beforeInits.toList.sorted
      .foldLeft(MObj())((o, h) => o update h(globals)(configs))

  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
