package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.trees.Tree
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ArrayBuffer

sealed trait TreeHook extends Hook:
  override def toString(): String = super.toString() + "-Tree"

trait TreeBeforeInit extends TreeHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait TreeBeforeLocals extends TreeHook with BeforeLocals:
  override def toString(): String = super.toString() + " before local"

trait TreeBeforeRender extends TreeHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait TreeAfterRender extends TreeHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait TreeAfterWrite extends TreeHook with AfterWrite[Tree[PostLike]]:
  override def toString(): String = super.toString() + " after write"

object TreeHooks
    extends HookObject[TreeHook]
    with WithBeforeInit[TreeHook, TreeBeforeInit]
    with WithBeforeLocals[TreeHook, TreeBeforeLocals]
    with WithBeforeRenders[TreeHook, TreeBeforeRender]
    with WithAfterRenders[TreeHook, TreeAfterRender]
    with WithAfterWrites[TreeHook, TreeAfterWrite, Tree[PostLike]]:

  protected val logger = Logger("TreeHooks")

  protected[hooks] def registerHook(hook: TreeHook) =
    hook match
      case hook: TreeBeforeInit =>
        _beforeInits += hook
        _bi = false
      case hook: TreeBeforeLocals =>
        _beforeLocals += hook
        _bl = false
      case hook: TreeBeforeRender =>
        _beforeRenders += hook
        _br = false
      case hook: TreeAfterRender =>
        _afterRenders += hook
        _ar = false
      case hook: TreeAfterWrite =>
        _afterWrites += hook
        _aw = false
