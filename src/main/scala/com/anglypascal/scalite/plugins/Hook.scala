package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.Site
import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.groups.Group
import com.anglypascal.scalite.layouts.Layout

import scala.collection.mutable.ListBuffer

/** A Hook has a priority and usually an apply function. Hooks are called at
  * various points of the site creation, and can be provided by the user to
  * exercise fine tuned control over the process.
  */
sealed trait Hook extends Plugin with Ordered[Hook]:
  val priority: Int
  def compare(that: Hook): Int = that.priority compare this.priority
  override def toString(): String = "Hook"

/** Object containing all defined Hooks and provides methods to join Hooks from
  * multiple lists of Hooks
  */
object Hooks:

  /** Register a new Hook */
  def registerHook(hook: Hook) =
    hook match
      case hook: ConverterHook  => ConverterHooks.registerHook(hook)
      case hook: CollectionHook => CollectionHooks.registerHook(hook)
      case hook: PostHook       => PostHooks.registerHook(hook)
      case hook: ReaderHook     => ReaderHooks.registerHook(hook)
      case hook: PageHook       => PageHooks.registerHook(hook)
      case hook: LayoutHook     => LayoutHooks.registerHook(hook)
      case hook: SiteHook       => SiteHooks.registerHook(hook)
      case hook: GroupHook      => GroupHooks.registerHook(hook)
      case null                 => ()

  /** Register many Hooks */
  def registerHooks(hooks: Hook*) =
    hooks foreach { registerHook(_) }

  /** Join multiple lists of Hooks */
  def join[A <: Hook](lists: List[A]*): List[A] =
    lists.foldRight(List[A]())(_ ::: _).sorted

/** To be run before an object is initiated.
  *
  * @param globals
  *   The global variables
  * @param config
  *   The local configurations
  * @returns
  *   A mutable DObj containing changes to be made to the config
  */
sealed trait BeforeInit extends Hook:
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
sealed trait BeforeLocals extends Hook:
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
sealed trait BeforeRender extends Hook:
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
sealed trait AfterRender extends Hook:
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
sealed trait AfterWrite[A] extends Hook:
  def apply(globals: IObj)(obj: A): Unit

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
  def apply(filetype: String, configs: IObj): MObj
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

  private val _beforeInits = ListBuffer[ConverterBeforeInit]()
  private val _beforeConverts = ListBuffer[ConverterBeforeConvert]()
  private val _afterConverts = ListBuffer[ConverterAfterConvert]()

  def registerHook(hook: ConverterHook) =
    hook match
      case hook: ConverterBeforeInit    => _beforeInits += hook
      case hook: ConverterBeforeConvert => _beforeConverts += hook
      case hook: ConverterAfterConvert  => _afterConverts += hook
      case null                         => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeConverts = _beforeConverts.toList.sorted
  def afterConverts = _afterConverts.toList.sorted

///////////
// Group //
///////////
sealed trait GroupHook extends Hook:
  override def toString(): String = super.toString() + "-Group"

trait GroupBeforeInit extends GroupHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait GroupBeforeLocal extends GroupHook with BeforeLocals:
  override def toString(): String = super.toString() + " before local"

trait GroupBeforeRender extends GroupHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait GroupAfterRender extends GroupHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait GroupAfterProcess extends GroupHook with AfterWrite[Group[PostLike]]:
  override def toString(): String = super.toString() + " after write"

object GroupHooks:

  private val _beforeInits = ListBuffer[GroupBeforeInit]()
  private val _beforeLocals = ListBuffer[GroupBeforeLocal]()
  private val _beforeRenders = ListBuffer[GroupBeforeRender]()
  private val _afterRenders = ListBuffer[GroupAfterRender]()
  private val _afterProcesses = ListBuffer[GroupAfterProcess]()

  def registerHook(hook: GroupHook) =
    hook match
      case hook: GroupBeforeInit   => _beforeInits += hook
      case hook: GroupBeforeLocal  => _beforeLocals += hook
      case hook: GroupBeforeRender => _beforeRenders += hook
      case hook: GroupAfterRender  => _afterRenders += hook
      case hook: GroupAfterProcess   => _afterProcesses += hook
      case null                    => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterProcesses.toList.sorted

////////////////
// Collection //
////////////////
sealed trait CollectionHook extends Hook:
  override def toString(): String = super.toString() + "-Collection"

trait CollectionBeforeInit extends CollectionHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait CollectionBeforeLocal extends CollectionHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait CollectionBeforeRender extends CollectionHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait CollectionAfterRender extends CollectionHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait CollectionAfterWrite extends CollectionHook with AfterWrite[Collection]:
  override def toString(): String = super.toString() + " after write"

object CollectionHooks:

  private val _beforeInits = ListBuffer[CollectionBeforeInit]()
  private val _beforeLocals = ListBuffer[CollectionBeforeLocal]()
  private val _beforeRenders = ListBuffer[CollectionBeforeRender]()
  private val _afterRenders = ListBuffer[CollectionAfterRender]()
  private val _afterWrites = ListBuffer[CollectionAfterWrite]()

  def registerHook(hook: CollectionHook) =
    hook match
      case hook: CollectionBeforeInit   => _beforeInits += hook
      case hook: CollectionBeforeLocal  => _beforeLocals += hook
      case hook: CollectionBeforeRender => _beforeRenders += hook
      case hook: CollectionAfterRender  => _afterRenders += hook
      case hook: CollectionAfterWrite   => _afterWrites += hook
      case null                         => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted

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

  private val _beforeInits = ListBuffer[LayoutBeforeInit]()
  private val _beforeRenders = ListBuffer[LayoutBeforeRender]()
  private val _afterRenders = ListBuffer[LayoutAfterRender]()

  def registerHook(hook: LayoutHook) =
    hook match
      case hook: LayoutBeforeInit   => _beforeInits += hook
      case hook: LayoutBeforeRender => _beforeRenders += hook
      case hook: LayoutAfterRender  => _afterRenders += hook
      case null                     => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted

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

////////////
// Reader //
////////////
sealed trait ReaderHook extends Hook:
  override def toString(): String = super.toString() + "-Reader"

trait ReaderBeforeInit extends ReaderHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait ReaderBeforeLocals extends ReaderHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait ReaderBeforeRender extends ReaderHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait ReaderAfterRender extends ReaderHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait ReaderAfterWrite extends ReaderHook with AfterWrite[Reader]:
  override def toString(): String = super.toString() + " after write"

object ReaderHooks:

  private val _beforeInits = ListBuffer[ReaderBeforeInit]()
  private val _beforeLocals = ListBuffer[ReaderBeforeLocals]()
  private val _beforeRenders = ListBuffer[ReaderBeforeRender]()
  private val _afterRenders = ListBuffer[ReaderAfterRender]()
  private val _afterWrites = ListBuffer[ReaderAfterWrite]()

  def registerHook(hook: ReaderHook) =
    hook match
      case hook: ReaderBeforeInit   => _beforeInits += hook
      case hook: ReaderBeforeLocals => _beforeLocals += hook
      case hook: ReaderBeforeRender => _beforeRenders += hook
      case hook: ReaderAfterRender  => _afterRenders += hook
      case hook: ReaderAfterWrite   => _afterWrites += hook
      case null                     => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted

//////////
// Post //
//////////
sealed trait PostHook extends Hook:
  override def toString(): String = super.toString() + "-Post"

trait PostBeforeInit extends PostHook with BeforeInit:
  override def toString(): String = super.toString() + " before init"

trait PostBeforeLocals extends PostHook with BeforeLocals:
  override def toString(): String = super.toString() + " before locals"

trait PostBeforeRender extends PostHook with BeforeRender:
  override def toString(): String = super.toString() + " before render"

trait PostAfterRender extends PostHook with AfterRender:
  override def toString(): String = super.toString() + " after render"

trait PostAfterWrite extends PostHook with AfterWrite[PostLike]:
  override def toString(): String = super.toString() + " after write"

object PostHooks:

  private val _beforeInits = ListBuffer[PostBeforeInit]()
  private val _beforeLocals = ListBuffer[PostBeforeLocals]()
  private val _beforeRenders = ListBuffer[PostBeforeRender]()
  private val _afterRenders = ListBuffer[PostAfterRender]()
  private val _afterWrites = ListBuffer[PostAfterWrite]()

  def registerHook(hook: PostHook) =
    hook match
      case hook: PostBeforeInit   => _beforeInits += hook
      case hook: PostBeforeLocals => _beforeLocals += hook
      case hook: PostBeforeRender => _beforeRenders += hook
      case hook: PostAfterRender  => _afterRenders += hook
      case hook: PostAfterWrite   => _afterWrites += hook
      case null                   => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted

//////////
// Page //
//////////
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

object PageHooks:

  private val _beforeInits = ListBuffer[PageBeforeInit]()
  private val _beforeLocals = ListBuffer[PageBeforeLocals]()
  private val _beforeRenders = ListBuffer[PageBeforeRender]()
  private val _afterRenders = ListBuffer[PageAfterRender]()
  private val _afterWrites = ListBuffer[PageAfterWrite]()

  def registerHook(hook: PageHook) =
    hook match
      case hook: PageBeforeInit   => _beforeInits += hook
      case hook: PageBeforeLocals => _beforeLocals += hook
      case hook: PageBeforeRender => _beforeRenders += hook
      case hook: PageAfterRender  => _afterRenders += hook
      case hook: PageAfterWrite   => _afterWrites += hook
      case null                   => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted
