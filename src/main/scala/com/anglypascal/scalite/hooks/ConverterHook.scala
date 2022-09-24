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

  protected val _beforeInits = ArrayBuffer[ConverterBeforeInit]()
  protected var _bi = true

  def beforeInits(globals: IObj)(filetype: String, config: IObj) =
    logger.trace(s"running before init hooks for $filetype")
    if !_bi then
      _beforeInits.sorted
      _bi = true
    _beforeInits.foldLeft(MObj())((o, h) =>
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

  protected val _beforeConverts = ArrayBuffer[ConverterBeforeConvert]()
  protected var _bc = true

  def beforeConverts(str: String, fp: String) =
    logger.trace(s"running before convert hooks for file $fp")
    if !_bc then
      _beforeConverts.sorted
      _bc = true
    _beforeConverts.foldLeft(str)((s, h) => h.apply(s, fp))

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

  protected val _afterConverts = ArrayBuffer[ConverterAfterConvert]()
  protected var _ac = true

  def afterConverts(str: String, fp: String) =
    logger.trace(s"running after convert hooks for file $fp")
    if !_ac then
      _afterConverts.sorted
      _ac = true
    _afterConverts.foldLeft(str)((s, h) => h.apply(s, fp))

object ConverterHooks
    extends HookObject[ConverterHook]
    with ConverterWithBeforeInit
    with ConverterWithBeforeRenders
    with ConverterWithAfterRenders:

  protected val logger = Logger("ConvertHooks")

  protected[hooks] def registerHook(hook: ConverterHook): Unit =
    hook match
      case hook: ConverterBeforeInit =>
        _beforeInits += hook
        _bi = false
      case hook: ConverterBeforeConvert =>
        _beforeConverts += hook
        _bc = false
      case hook: ConverterAfterConvert =>
        _afterConverts += hook
        _ac = false
      case null => ()
