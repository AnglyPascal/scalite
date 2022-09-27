package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ArrayBuffer
import com.typesafe.scalalogging.Logger

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

object PageHooks
    extends HookObject[PageHook]
    with WithBeforeInit[PageHook, PageBeforeInit]
    with WithBeforeLocals[PageHook, PageBeforeLocals]
    with WithBeforeRenders[PageHook, PageBeforeRender]
    with WithAfterRenders[PageHook, PageAfterRender]
    with WithAfterWrites[PageHook, PageAfterWrite, Page]:

  protected val logger = Logger("PageHooks")

  protected[hooks] def registerHook(hook: PageHook) =
    hook match
      case hook: PageBeforeInit => add(hook)
      case hook: PageBeforeLocals => add(hook)
      case hook: PageBeforeRender => add(hook)
      case hook: PageAfterRender => add(hook)
      case hook: PageAfterWrite => add(hook)
