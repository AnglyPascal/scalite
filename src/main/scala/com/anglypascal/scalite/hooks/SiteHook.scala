package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.Site
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ArrayBuffer
import com.typesafe.scalalogging.Logger

sealed trait SiteHook extends Hook:
  override def toString(): String = super.toString() + "-Site"

/** To be run right after the site is initiated
  *
  * @param globals
  *   The global variales of the site
  * @returns
  *   A mutable DObj containing the changes to be made to the globals
  */
trait SiteAfterInit extends SiteHook:
  def apply(globals: IObj): MObj
  override def toString(): String = super.toString() + " after init"

trait SiteWithAfterInit:
  this: HookObject[SiteHook] =>

  protected val _afterInits = ArrayBuffer[SiteAfterInit]()
  protected var _ai = true

  def afterInits(globals: IObj) =
    logger.trace(s"running before init hooks")
    if !_ai then
      _afterInits.sorted
      _ai = true
    _afterInits.foldLeft(MObj())((o, h) => o update h(globals))

/** To be run right after the site is reset for clean build
  *
  * @param globals
  *   The global variales of the site
  * @returns
  *   A mutable DObj containing the changes to be made to the globals
  */
trait SiteAfterReset extends SiteHook:
  def apply(globals: IObj): MObj
  override def toString(): String = super.toString() + " after reset"

trait SiteWithAfterReset:
  this: HookObject[SiteHook] =>

  protected val _afterResets = ArrayBuffer[SiteAfterReset]()
  protected var _ars = true

  def afterResets(globals: IObj)(filetype: String, config: IObj) =
    logger.trace(s"running before init hooks for $filetype")
    if !_ars then
      _afterResets.sorted
      _ars = true
    _afterResets.foldLeft(MObj())((o, h) => o update h(globals))

/** To be run right after the files of this site are all read
  *
  * @param globals
  *   The global variales of the site
  * @returns
  *   A mutable DObj containing the changes to be made to the globals
  */
trait SiteAfterRead extends SiteHook:
  def apply(globals: IObj): MObj
  override def toString(): String = super.toString() + " after read"

trait SiteWithAfterRead:
  this: HookObject[SiteHook] =>

  protected val _afterReads = ArrayBuffer[SiteAfterRead]()
  protected var _ard = true

  def afterReads(globals: IObj)(filetype: String, config: IObj) =
    logger.trace(s"running before init hooks for $filetype")
    if !_ard then
      _afterReads.sorted
      _ard = true
    _afterReads.foldLeft(MObj())((o, h) => o update h(globals))

trait SiteBeforeRender extends SiteHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait SiteAfterRender extends SiteHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait SiteAfterWrite extends SiteHook with AfterWrite[Site]:
  override def toString(): String = super.toString() + " after write"

object SiteHooks
    extends HookObject[SiteHook]
    with SiteWithAfterInit
    with SiteWithAfterRead
    with SiteWithAfterReset
    with WithBeforeRenders[SiteHook, SiteBeforeRender]
    with WithAfterRenders[SiteHook, SiteAfterRender]
    with WithAfterWrites[SiteHook, SiteAfterWrite, Site]:

  protected val logger = Logger("SiteHooks")

  protected[hooks] def registerHook(hook: SiteHook) =
    hook match
      case hook: SiteAfterInit =>
        _afterInits += hook
        _ai = false
      case hook: SiteAfterReset =>
        _afterResets += hook
        _ars = false
      case hook: SiteAfterRead =>
        _afterReads += hook
        _ard = false
      case hook: SiteBeforeRender =>
        _beforeRenders += hook
        _br = false
      case hook: SiteAfterRender =>
        _afterRenders += hook
        _ar = false
      case hook: SiteAfterWrite =>
        _afterWrites += hook
        _aw = false
