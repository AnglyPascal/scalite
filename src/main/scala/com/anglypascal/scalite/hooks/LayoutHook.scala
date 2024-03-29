package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ArrayBuffer
import com.typesafe.scalalogging.Logger

sealed trait LayoutHook extends Hook:
  override def toString(): String = super.toString() + "-Layout"

/** To be run before the layout is initiated.
  *
  * @param lang
  *   The language of this layout
  * @param name
  *   The name of this layout
  * @param filepath
  *   The filepath to the layout file
  * @returns
  *   A mutable DObj containing changes to be made to the configuration of this
  *   layout
  */
trait LayoutBeforeInit extends LayoutHook:
  def apply(lang: String, name: String)(filepath: String): MObj
  override def toString(): String = super.toString() + " before init"

trait LayoutWithBeforeInit:
  this: HookObject[LayoutHook] =>

  private val sh = SortedHooks[LayoutBeforeInit]
  protected def add(h: LayoutBeforeInit): Unit = sh.add(h)

  def beforeInits(lang: String, name: String)(filepath: String) =
    logger.trace("running before inits")
    sh.sortedArray foreach { _.apply(lang, name)(filepath) }

/** To be run before the layout is rendered.
  *
  * @param context
  *   The context holding placeholder variables to be used in rendering the
  *   layout
  * @param content
  *   The contents of the child of this layout
  * @returns
  *   A mutable DObj containing changes to be made to the context
  */
trait LayoutBeforeRender extends LayoutHook:
  def apply(lang: String, name: String)(context: IObj, content: String): MObj
  override def toString(): String = super.toString() + " before render"

trait LayoutWithBeforeRenders:
  this: HookObject[LayoutHook] =>

  private val sh = SortedHooks[LayoutBeforeRender]
  protected def add(h: LayoutBeforeRender): Unit = sh.add(h)

  def beforeRenders(lang: String, name: String)(
      context: IObj,
      content: String
  ) =
    logger.trace("running before renders")
    sh.sortedArray.foldLeft(MObj())((o, h) =>
      o update h(lang, name)(context, content)
    )

/** To be run after the layout is rendered.
  *
  * @param str
  *   The rendered string of this layout
  * @returns
  *   A filtered string
  */
trait LayoutAfterRender extends LayoutHook:
  def apply(lang: String, name: String)(str: String): String
  override def toString(): String = super.toString() + " after render"

trait LayoutWithAfterRenders:
  this: HookObject[LayoutHook] =>

  private val sh = SortedHooks[LayoutAfterRender]
  protected def add(h: LayoutAfterRender): Unit = sh.add(h)

  def afterRenders(lang: String, name: String)(rendered: String) =
    logger.trace("running after renders")
    sh.sortedArray.foldLeft(rendered)((s, h) => h(lang, name)(s))

object LayoutHooks
    extends HookObject[LayoutHook]
    with LayoutWithBeforeInit
    with LayoutWithBeforeRenders
    with LayoutWithAfterRenders:

  protected val logger = Logger("LayoutHooks")

  protected[hooks] def registerHook(hook: LayoutHook) =
    hook match
      case hook: LayoutBeforeInit   => add(hook)
      case hook: LayoutBeforeRender => add(hook)
      case hook: LayoutAfterRender  => add(hook)
