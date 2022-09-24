package com.anglypascal.scalite.hooks

import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.plugins.Plugin

import com.typesafe.scalalogging.Logger

/** A Hook has a priority and usually an apply function. Hooks are called at
  * various points of the site creation, and can be provided by the user to
  * exercise fine tuned control over the process.
  */
trait Hook extends Plugin with Ordered[Hook]:
  val priority: Int
  def compare(that: Hook): Int = that.priority compare this.priority
  override def toString(): String = "Hook"

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

