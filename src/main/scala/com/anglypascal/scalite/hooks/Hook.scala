package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.plugins.Plugin
import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ArrayBuffer

/** A Hook has a priority and usually an apply function. Hooks are called at
  * various points of the site creation, and can be provided by the user to
  * exercise fine tuned control over the process.
  */
trait Hook extends Plugin with Ordered[Hook]:
  val priority: Int
  def compare(that: Hook): Int = that.priority compare this.priority
  override def toString(): String = "Hook"

trait HookObject[H <: Hook]:
  protected val logger: Logger
  protected[hooks] def registerHook(hook: H): Unit

/** To be run before an object is initiated.
  *
  * @param globals
  *   The global variables
  * @param config
  *   The local configurations
  * @returns
  *   A mutable DObj containing changes to be made to the config
  */
trait BeforeInit extends Hook:
  def apply(globals: IObj)(config: IObj): MObj

trait WithBeforeInit[H <: Hook, B <: BeforeInit]:
  this: HookObject[H] =>

  protected val _beforeInits = ArrayBuffer[B]()
  protected var _bi = true

  def beforeInits(globals: IObj)(configs: IObj) =
    logger.trace("running before inits")
    if !_bi then
      _beforeInits.sorted
      _bi = true
    _beforeInits.foldLeft(MObj())((o, h) => o update h(globals)(configs))

/** To be run before the local variables of an object are initiated.
  *
  * @param globals
  *   The global variables
  * @param locals
  *   The current local variables
  * @returns
  *   A mutable DObj containing changes to be made to the local variables
  */
trait BeforeLocals extends Hook:
  def apply(globals: IObj)(locals: IObj): MObj

trait WithBeforeLocals[H <: Hook, B <: BeforeLocals]:
  this: HookObject[H] =>

  protected val _beforeLocals = ArrayBuffer[B]()
  protected var _bl = true

  def beforeLocals(globals: IObj)(locals: IObj) =
    logger.trace("running before locals")
    if !_bl then
      _beforeLocals.sorted
      _bl = true
    _beforeLocals.foldLeft(MObj())((o, h) => o update h(globals)(locals))

/** To be run before the contents of an object are rendered.
  *
  * @param globals
  *   The global variables
  * @param context
  *   The placeholder variables to be used in the rendering process
  * @returns
  *   A mutable DObj containing changes to be made to the context
  */
trait BeforeRender extends Hook:
  def apply(globals: IObj)(context: IObj): MObj

trait WithBeforeRenders[H <: Hook, B <: BeforeRender]:
  this: HookObject[H] =>

  protected val _beforeRenders = ArrayBuffer[B]()
  protected var _br = true

  def beforeRenders(globals: IObj)(context: IObj) =
    logger.trace("running before renders")
    if !_br then
      _beforeRenders.sorted
      _br = true
    _beforeRenders.foldLeft(MObj())((o, h) => o update h(globals)(context))

/** To be run after the contents of an object are rendered.
  *
  * @param globals
  *   The global variables
  * @param locals
  *   The local variables of the object
  * @param rendered
  *   The rendered output
  * @returns
  *   The filtered version of the rendered output
  */
trait AfterRender extends Hook:
  def apply(globals: IObj)(locals: IObj, rendered: String): String

trait WithAfterRenders[H <: Hook, A <: AfterRender]:
  this: HookObject[H] =>

  protected val _afterRenders = ArrayBuffer[A]()
  protected var _ar = true

  def afterRenders(globals: IObj)(locals: IObj, rendered: String) =
    logger.trace("running after renders")
    if !_ar then
      _afterRenders.sorted
      _ar = true
    _afterRenders.foldLeft(rendered)((s, h) => h(globals)(locals, s))

/** To be run after the contents of an object of type A are written to disk.
  *
  * @tparam A
  *   The type of the object running this Hook
  * @param globals
  *   The global variables
  * @param obj
  *   The current object
  */
trait AfterWrite[A] extends Hook:
  def apply(globals: IObj)(obj: A): Unit

trait WithAfterWrites[H <: Hook, T <: AfterWrite[A], A]:
  this: HookObject[H] =>

  protected val _afterWrites = ArrayBuffer[T]()
  protected var _aw = true

  def afterWrites(globals: IObj)(obj: A) =
    logger.trace("running after writes")
    if !_aw then
      _afterWrites.sorted
      _aw = true
    _afterWrites foreach { _(globals)(obj) }

/** Object containing all defined Hooks and provides methods to join Hooks from
  * multiple lists of Hooks
  */
object Hooks:

  private val logger = Logger("Hooks")

  /** Register a new Hook */
  def registerHook(hook: Hook) =
    hook match
      case hook: ConverterHook  => ConverterHooks.registerHook(hook)
      case hook: CollectionHook => CollectionHooks.registerHook(hook)
      case hook: PostHook       => PostHooks.registerHook(hook)
      case hook: ItemHook       => ItemHooks.registerHook(hook)
      case hook: PageHook       => PageHooks.registerHook(hook)
      case hook: LayoutHook     => LayoutHooks.registerHook(hook)
      case hook: TreeHook       => TreeHooks.registerHook(hook)
      case hook: SiteHook       => SiteHooks.registerHook(hook)
      case _                    => ()

  /** Register many Hooks */
  def registerHooks(hooks: Hook*) =
    hooks foreach { registerHook(_) }

  /** Join multiple lists of Hooks */
  def join[A <: Hook](lists: List[A]*): List[A] =
    lists.foldRight(List[A]())(_ ::: _).sorted
