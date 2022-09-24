package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger

//////////
// Post //
//////////
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

object PostHooks:

  private val logger = Logger("PostHooks")

  private val _beforeInits = ListBuffer[PostBeforeInit]()
  private val _beforeLocals = ListBuffer[PostBeforeLocals]()
  private val _beforeRenders = ListBuffer[PostBeforeRender]()
  private val _afterRenders = ListBuffer[PostAfterRender]()
  private val _afterWrites = ListBuffer[PostAfterWrite]()

  def registerHook(hook: PostHook) =
    hook match
      case hook: PostBeforeInit   => _beforeInits += hook
      case hook: PostBeforeLocals => _beforeLocals += hook
      case hook: PostBeforeRender => _beforeRenders += hook
      case hook: PostAfterRender  => _afterRenders += hook
      case hook: PostAfterWrite   => _afterWrites += hook
      case null                   => ()

  def beforeInits(globals: IObj)(configs: IObj) =
    _beforeInits.toList.sorted
      .foldLeft(MObj())((o, h) => o update h(globals)(configs))

  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted

