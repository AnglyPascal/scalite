package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import scala.collection.mutable.ArrayBuffer
import com.typesafe.scalalogging.Logger

sealed trait ConverterHook extends Hook:
  override def toString(): String = super.toString() + "-Converter"

/** To be run before the initiation of the Converter
  *
  * @param filetype
  *   The filetype of the Converter
  * @param configs
  *   The configuration variables of the Converter
  * @returns
  *   A mutable DObj containing the changes to be made to the configs
  */
trait ConverterBeforeInit extends ConverterHook:
  def apply(globals: IObj)(filetype: String, configs: IObj): MObj
  override def toString(): String = super.toString() + " before init"

trait ConverterWithBeforeInit:
  this: HookObject[ConverterHook] =>

  private val sh = SortedHooks[ConverterBeforeInit]
  protected def add(h: ConverterBeforeInit): Unit = sh.add(h)

  def beforeInits(globals: IObj)(filetype: String, config: IObj) =
    logger.trace(s"running before init hooks for $filetype")
    sh.sortedArray.foldLeft(MObj())((o, h) =>
      o update h(globals)(filetype, config)
    )

/** To be run before the conversion of the string
  *
  * @param str
  *   The contents of the file
  * @param filepath
  *   The path to the file
  * @returns
  *   A filtered string
  */
trait ConverterBeforeConvert extends ConverterHook:
  def apply(str: String, filepath: String): String
  override def toString(): String = super.toString() + " before convert"

trait ConverterWithBeforeRenders:
  this: HookObject[ConverterHook] =>

  private val sh = SortedHooks[ConverterBeforeConvert]
  protected def add(h: ConverterBeforeConvert): Unit = sh.add(h)

  def beforeConverts(str: String, fp: String) =
    logger.trace(s"running before convert hooks for file $fp")
    sh.sortedArray.foldLeft(str)((s, h) => h.apply(s, fp))

/** To be run before the conversion of the string
  *
  * @param str
  *   The converted contents of the file
  * @param filepath
  *   The path to the file
  * @returns
  *   A filtered string
  */
trait ConverterAfterConvert extends ConverterHook:
  def apply(str: String, filepath: String): String
  override def toString(): String = super.toString() + " after convert"

trait ConverterWithAfterRenders:
  this: HookObject[ConverterHook] =>

  private val sh = SortedHooks[ConverterAfterConvert]
  protected def add(h: ConverterAfterConvert): Unit = sh.add(h)

  def afterConverts(str: String, fp: String) =
    sh.sortedArray.foldLeft(str)((s, h) => h.apply(s, fp))

object ConverterHooks
    extends HookObject[ConverterHook]
    with ConverterWithBeforeInit
    with ConverterWithBeforeRenders
    with ConverterWithAfterRenders:

  protected val logger = Logger("ConvertHooks")

  protected[hooks] def registerHook(hook: ConverterHook): Unit =
    hook match
      case hook: ConverterBeforeInit    => add(hook)
      case hook: ConverterBeforeConvert => add(hook)
      case hook: ConverterAfterConvert  => add(hook)
