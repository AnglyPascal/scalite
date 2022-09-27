package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ArrayBuffer

sealed trait CollectionHook extends Hook:
  override def toString(): String = super.toString() + "-Collection"

trait CollectionBeforeInit extends CollectionHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait CollectionBeforeLocals extends CollectionHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait CollectionBeforeRender extends CollectionHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait CollectionAfterRender extends CollectionHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait CollectionAfterWrite extends CollectionHook with AfterWrite[Collection]:
  override def toString(): String = super.toString() + " after write"

object CollectionHooks
    extends HookObject[CollectionHook]
    with WithBeforeInit[CollectionHook, CollectionBeforeInit]
    with WithBeforeLocals[CollectionHook, CollectionBeforeLocals]
    with WithBeforeRenders[CollectionHook, CollectionBeforeRender]
    with WithAfterRenders[CollectionHook, CollectionAfterRender]
    with WithAfterWrites[CollectionHook, CollectionAfterWrite, Collection]:

  protected val logger = Logger("CollectionHooks")

  protected[hooks] def registerHook(hook: CollectionHook) =
    hook match
      case hook: CollectionBeforeInit => add(hook)
      case hook: CollectionBeforeLocals => add(hook)
      case hook: CollectionBeforeRender => add(hook)
      case hook: CollectionAfterRender => add(hook)
      case hook: CollectionAfterWrite => add(hook)
