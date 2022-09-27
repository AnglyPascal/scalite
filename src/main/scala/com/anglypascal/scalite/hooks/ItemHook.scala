package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.collections.ItemLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ArrayBuffer

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

object ItemHooks
    extends HookObject[ItemHook]
    with WithBeforeInit[ItemHook, ItemBeforeInit]
    with WithBeforeLocals[ItemHook, ItemBeforeLocals]
    with WithBeforeRenders[ItemHook, ItemBeforeRender]
    with WithAfterRenders[ItemHook, ItemAfterRender]:

  protected val logger = Logger("ItemHooks")

  protected[hooks] def registerHook(hook: ItemHook) =
    hook match
      case hook: ItemBeforeInit => add(hook)
      case hook: ItemBeforeLocals => add(hook)
      case hook: ItemBeforeRender => add(hook)
      case hook: ItemAfterRender => add(hook)
