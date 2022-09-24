package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.Site
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger

//////////
// Site //
//////////
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

trait SiteBeforeRender extends SiteHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait SiteAfterRender extends SiteHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait SiteAfterWrite extends SiteHook with AfterWrite[Site]:
  override def toString(): String = super.toString() + " after write"

object SiteHooks:

  private val logger = Logger("SiteHooks")

  private val _afterInits = ListBuffer[SiteAfterInit]()
  private val _afterReads = ListBuffer[SiteAfterRead]()
  private val _beforeRenders = ListBuffer[SiteBeforeRender]()
  private val _afterRenders = ListBuffer[SiteAfterRender]()
  private val _afterWrites = ListBuffer[SiteAfterWrite]()

  def registerHook(hook: SiteHook) =
    hook match
      case hook: SiteAfterInit    => _afterInits += hook
      case hook: SiteAfterRead    => _afterReads += hook
      case hook: SiteBeforeRender => _beforeRenders += hook
      case hook: SiteAfterRender  => _afterRenders += hook
      case hook: SiteAfterWrite   => _afterWrites += hook
      case _                      => ()

  def afterInits = _afterInits.toList.sorted
  def afterReads = _afterReads.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted
