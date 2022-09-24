package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger
import com.anglypascal.scalite.trees.Tree
import com.anglypascal.scalite.collections.PostLike

///////////
// Tree //
///////////
sealed trait TreeHook extends Hook:
  override def toString(): String = super.toString() + "-Tree"

trait TreeBeforeInit extends TreeHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait TreeBeforeLocal extends TreeHook with BeforeLocals:
  override def toString(): String = super.toString() + " before local"

trait TreeBeforeRender extends TreeHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait TreeAfterRender extends TreeHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait TreeAfterProcess extends TreeHook with AfterWrite[Tree[PostLike]]:
  override def toString(): String = super.toString() + " after write"

object TreeHooks:

  private val logger = Logger("TreeHooks")

  private val _beforeInits = ListBuffer[TreeBeforeInit]()
  private val _beforeLocals = ListBuffer[TreeBeforeLocal]()
  private val _beforeRenders = ListBuffer[TreeBeforeRender]()
  private val _afterRenders = ListBuffer[TreeAfterRender]()
  private val _afterProcesses = ListBuffer[TreeAfterProcess]()

  def registerHook(hook: TreeHook) =
    hook match
      case hook: TreeBeforeInit   => _beforeInits += hook
      case hook: TreeBeforeLocal  => _beforeLocals += hook
      case hook: TreeBeforeRender => _beforeRenders += hook
      case hook: TreeAfterRender  => _afterRenders += hook
      case hook: TreeAfterProcess => _afterProcesses += hook
      case null                   => ()

  def beforeInits(globals: IObj)(configs: IObj) =
    _beforeInits.toList.sorted
      .foldLeft(MObj())((o, h) => o update h(globals)(configs))

  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterProcesses.toList.sorted
