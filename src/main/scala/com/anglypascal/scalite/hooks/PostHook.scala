package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ArrayBuffer
import com.typesafe.scalalogging.Logger

sealed trait PostHook extends Hook:
  override def toString(): String = super.toString() + "-Post"

trait PostBeforeInit extends PostHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait PostBeforeLocals extends PostHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait PostBeforeRender extends PostHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait PostAfterRender extends PostHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait PostAfterWrite extends PostHook with AfterWrite[PostLike]:
  override def toString(): String = super.toString() + " after write"

object PostHooks
    extends HookObject[PostHook]
    with WithBeforeInit[PostHook, PostBeforeInit]
    with WithBeforeLocals[PostHook, PostBeforeLocals]
    with WithBeforeRenders[PostHook, PostBeforeRender]
    with WithAfterRenders[PostHook, PostAfterRender]
    with WithAfterWrites[PostHook, PostAfterWrite, PostLike]:

  protected val logger = Logger("PostHooks")

  protected[hooks] def registerHook(hook: PostHook) =
    hook match
      case hook: PostBeforeInit =>
        _beforeInits += hook
        _bi = false
      case hook: PostBeforeLocals =>
        _beforeLocals += hook
        _bl = false
      case hook: PostBeforeRender =>
        _beforeRenders += hook
        _br = false
      case hook: PostAfterRender =>
        _afterRenders += hook
        _ar = false
      case hook: PostAfterWrite =>
        _afterWrites += hook
        _aw = false
