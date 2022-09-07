package com.anglypascal.scalite.plugins

import com.anglypascal.scalite.Site
import com.anglypascal.scalite.collections.Collection
import com.anglypascal.scalite.collections.PostLike
import com.anglypascal.scalite.data.immutable.{DObj => IObj}
import com.anglypascal.scalite.data.mutable.{DObj => MObj}
import com.anglypascal.scalite.documents.Page
import com.anglypascal.scalite.documents.Reader
import com.anglypascal.scalite.layouts.Layout

import scala.collection.mutable.ListBuffer
import com.anglypascal.scalite.groups.Group

sealed trait Hook extends Plugin with Ordered[Hook]:
  val priority: Int
  def compare(that: Hook): Int = this.priority compare that.priority

object Hooks:
  def registerHook(hook: Hook) =
    hook match
      case hook: ConverterHook  => ConverterHooks.registerHook(hook)
      case hook: CollectionHook => CollectionHooks.registerHook(hook)
      case hook: PostHook       => PostHooks.registerHook(hook)
      case hook: ReaderHook     => ReaderHooks.registerHook(hook)
      case hook: PageHook       => PageHooks.registerHook(hook)
      case hook: LayoutHook     => LayoutHooks.registerHook(hook)
      case hook: SiteHook       => SiteHooks.registerHook(hook)
      case null                 => ()

  def registerHooks(hooks: Hook*) =
    hooks foreach { registerHook(_) }

  def join[A <: Hook](lists: List[A]*): List[A] =
    lists.foldRight(List[A]())(_ ::: _).sorted

sealed trait BeforeInit extends Hook:
  def apply(globals: IObj)(config: IObj): Unit

sealed trait BeforeLocals extends Hook:
  def apply(globals: IObj)(locals: IObj): MObj

sealed trait BeforeRender extends Hook:
  def apply(globals: IObj)(context: IObj): Unit

sealed trait AfterRender extends Hook:
  def apply(globals: IObj)(locals: IObj, rendered: String): Unit

sealed trait AfterWrite[A] extends Hook:
  def apply(globals: IObj)(page: A): Unit

sealed trait ConverterHook extends Hook

trait ConverterBeforeInit extends ConverterHook:
  def apply(fileType: String, configs: IObj): Unit

trait ConverterBeforeConvert extends ConverterHook:
  def apply(str: String, filepath: String): Unit

trait ConverterAfterConvert extends ConverterHook:
  def apply(str: String): Unit

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

sealed trait GroupHook extends Hook

trait GroupBeforeInit extends GroupHook with BeforeInit
trait GroupBeforeLocal extends GroupHook with BeforeLocals
trait GroupBeforeRender extends GroupHook with BeforeRender
trait GroupAfterRender extends GroupHook with AfterRender
trait GroupAfterWrite extends GroupHook with AfterWrite[Group[PostLike]]

object GroupHooks:

  private val _beforeInits = ListBuffer[GroupBeforeInit]()
  private val _beforeLocals = ListBuffer[GroupBeforeLocal]()
  private val _beforeRenders = ListBuffer[GroupBeforeRender]()
  private val _afterRenders = ListBuffer[GroupAfterRender]()
  private val _afterWrites = ListBuffer[GroupAfterWrite]()

  def registerHook(hook: GroupHook) =
    hook match
      case hook: GroupBeforeInit   => _beforeInits += hook
      case hook: GroupBeforeLocal  => _beforeLocals += hook
      case hook: GroupBeforeRender => _beforeRenders += hook
      case hook: GroupAfterRender  => _afterRenders += hook
      case hook: GroupAfterWrite   => _afterWrites += hook
      case null                         => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeLocals = _beforeLocals.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted
  def afterWrites = _afterWrites.toList.sorted


sealed trait CollectionHook extends Hook

trait CollectionBeforeInit extends CollectionHook with BeforeInit
trait CollectionBeforeLocal extends CollectionHook with BeforeLocals
trait CollectionBeforeRender extends CollectionHook with BeforeRender
trait CollectionAfterRender extends CollectionHook with AfterRender
trait CollectionAfterWrite extends CollectionHook with AfterWrite[Collection]

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

sealed trait LayoutHook extends Hook

trait LayoutBeforeInit extends LayoutHook:
  def apply(lang: String, name: String, filepath: String): Unit

trait LayoutBeforeRender extends LayoutHook:
  def apply(context: IObj, content: String = ""): Unit

trait LayoutAfterRender extends LayoutHook:
  def apply(str: String): String

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

sealed trait SiteHook extends Hook

trait SiteAfterInit extends SiteHook:
  def apply(globals: IObj): MObj

trait SiteAfterReset extends SiteHook:
  def apply(globals: IObj): Unit

trait SiteAfterRead extends SiteHook:
  def apply(globals: IObj): Unit

trait SiteBeforeRender extends SiteHook with BeforeRender
trait SiteAfterRender extends SiteHook with AfterRender
trait SiteAfterWrite extends SiteHook with AfterWrite[Site]

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

sealed trait ReaderHook extends Hook

trait ReaderBeforeInit extends ReaderHook with BeforeInit
trait ReaderBeforeLocals extends ReaderHook with BeforeLocals
trait ReaderBeforeRender extends ReaderHook with BeforeRender
trait ReaderAfterRender extends ReaderHook with AfterRender
trait ReaderAfterWrite extends ReaderHook with AfterWrite[Reader]

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

sealed trait PostHook extends Hook

trait PostBeforeInit extends PostHook with BeforeInit
trait PostBeforeLocals extends PostHook with BeforeLocals
trait PostBeforeRender extends PostHook with BeforeRender
trait PostAfterRender extends PostHook with AfterRender
trait PostAfterWrite extends PostHook with AfterWrite[PostLike]

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

sealed trait PageHook extends Hook

trait PageBeforeInit extends PageHook with BeforeInit
trait PageBeforeLocals extends PageHook with BeforeLocals
trait PageBeforeRender extends PageHook with BeforeRender
trait PageAfterRender extends PageHook with AfterRender
trait PageAfterWrite extends PageHook with AfterWrite[Page]

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
