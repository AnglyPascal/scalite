package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger


////////////////
// Collection //
////////////////
sealed trait CollectionHook extends Hook:
  override def toString(): String = super.toString() + "-Collection"

trait CollectionBeforeInit extends CollectionHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait CollectionBeforeLocal extends CollectionHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait CollectionBeforeRender extends CollectionHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait CollectionAfterRender extends CollectionHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait CollectionAfterWrite extends CollectionHook with AfterWrite[Collection]:
  override def toString(): String = super.toString() + " after write"

object CollectionHooks:

  private val logger = Logger("CollectionHooks")

  private val _beforeInits = ListBuffer[CollectionBeforeInit]()
  private val _beforeLocals = ListBuffer[CollectionBeforeLocal]()
  private val _beforeRenders = ListBuffer[CollectionBeforeRender]()
  private val _afterRenders = ListBuffer[CollectionAfterRender]()
  private val _afterWrites = ListBuffer[CollectionAfterWrite]()

  def registerHook(hook: CollectionHook) =
    hook match
      case hook: CollectionBeforeInit   => _beforeInits += hook
      case hook: CollectionBeforeLocal  => _beforeLocals += hook
      case hook: CollectionBeforeRender => _beforeRenders += hook
      case hook: CollectionAfterRender  => _afterRenders += hook
      case hook: CollectionAfterWrite   => _afterWrites += hook
      case null                         => ()

  def beforeInits(globals: IObj)(configs: IObj) =
    _beforeInits.toList.sorted
      .foldLeft(MObj())((o, h) => o update h(globals)(configs))

  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted

