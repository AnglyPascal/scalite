package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.Logger

///////////////
// Converter //
///////////////
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

object ConverterHooks:

  private val logger = Logger("ConvertHooks")

  private val _beforeInits = ListBuffer[ConverterBeforeInit]()
  private var _bISorted = false
  private val _beforeConverts = ListBuffer[ConverterBeforeConvert]()
  private var _bCSorted = false
  private val _afterConverts = ListBuffer[ConverterAfterConvert]()
  private var _aCSorted = false

  def registerHook(hook: ConverterHook): Unit =
    hook match
      case hook: ConverterBeforeInit =>
        _beforeInits += hook
        _bISorted = false
      case hook: ConverterBeforeConvert =>
        _beforeConverts += hook
        _bCSorted = false
      case hook: ConverterAfterConvert =>
        _afterConverts += hook
        _aCSorted = false
      case null => ()

  def beforeInits(globals: IObj)(filetype: String, config: IObj) =
    logger.trace(s"running before init hooks for $filetype")
    if !_bISorted then
      _beforeInits.sorted
      _bISorted = true
    _beforeInits.foldLeft(MObj())((o, h) =>
      o update h(globals)(filetype, config)
    )

  def beforeConverts(str: String, fp: String) =
    logger.trace(s"running before convert hooks for file $fp")
    if !_bCSorted then
      _beforeConverts.sorted
      _bCSorted = true
    _beforeConverts.foldLeft(str)((s, h) => h.apply(s, fp))

  def afterConverts(str: String, fp: String) =
    logger.trace(s"running after convert hooks for file $fp")
    if !_aCSorted then
      _afterConverts.sorted
      _aCSorted = true
    _afterConverts.foldLeft(str)((s, h) => h.apply(s, fp))
