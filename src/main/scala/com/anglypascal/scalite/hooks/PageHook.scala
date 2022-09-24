package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger

//////////
// Page //
//////////
sealed trait PageHook extends Hook:
  override def toString(): String = super.toString() + "-Page"

trait PageBeforeInit extends PageHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait PageBeforeLocals extends PageHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait PageBeforeRender extends PageHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait PageAfterRender extends PageHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait PageAfterWrite extends PageHook with AfterWrite[Page]:
  override def toString(): String = super.toString() + " after write"

object PageHooks:

  private val logger = Logger("PageHooks")

  private val _beforeInits = ListBuffer[PageBeforeInit]()
  private val _beforeLocals = ListBuffer[PageBeforeLocals]()
  private val _beforeRenders = ListBuffer[PageBeforeRender]()
  private val _afterRenders = ListBuffer[PageAfterRender]()
  private val _afterWrites = ListBuffer[PageAfterWrite]()

  def registerHook(hook: PageHook) =
    hook match
      case hook: PageBeforeInit   => _beforeInits += hook
      case hook: PageBeforeLocals => _beforeLocals += hook
      case hook: PageBeforeRender => _beforeRenders += hook
      case hook: PageAfterRender  => _afterRenders += hook
      case hook: PageAfterWrite   => _afterWrites += hook
      case null                   => ()

  def beforeInits(globals: IObj)(configs: IObj) =
    _beforeInits.toList.sorted
      .foldLeft(MObj())((o, h) => o update h(globals)(configs))

  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted
