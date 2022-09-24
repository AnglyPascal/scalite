package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.layouts.Layout
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}

import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger

////////////
// Layout //
////////////
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
  def apply(lang: String, name: String, filepath: String): MObj
  override def toString(): String = super.toString() + " before init"

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
  def apply(context: IObj, content: String = ""): MObj
  override def toString(): String = super.toString() + " before render"

/** To be run after the layout is rendered.
  *
  * @param str
  *   The rendered string of this layout
  * @returns
  *   A filtered string
  */
trait LayoutAfterRender extends LayoutHook:
  def apply(str: String): String
  override def toString(): String = super.toString() + " after render"

object LayoutHooks:

  private val logger = Logger("LayoutHooks")

  private val _beforeInits = ListBuffer[LayoutBeforeInit]()
  private val _beforeRenders = ListBuffer[LayoutBeforeRender]()
  private val _afterRenders = ListBuffer[LayoutAfterRender]()

  def registerHook(hook: LayoutHook) =
    hook match
      case hook: LayoutBeforeInit   => _beforeInits += hook
      case hook: LayoutBeforeRender => _beforeRenders += hook
      case hook: LayoutAfterRender  => _afterRenders += hook
      case null                     => ()

  def beforeInits(lang: String, name: String, filepath: String) =
    _beforeInits.toList.sorted
      .foreach { _.apply(lang, name, filepath) }

  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
