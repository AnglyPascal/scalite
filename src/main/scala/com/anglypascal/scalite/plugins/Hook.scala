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

sealed trait Hook extends Plugin with Ordered[Hook]:
  val priority: Int
  def compare(that: Hook): Int = this.priority compare that.priority

object Hooks:
  def addHook(hook: Hook) =
    hook match
      case hook: ConverterHook  => ConverterHooks.addHook(hook)
      case hook: CollectionHook => CollectionHooks.addHook(hook)
      case hook: PostHook       => PostHooks.addHook(hook)
      case hook: ReaderHook     => ReaderHooks.addHook(hook)
      case hook: PageHook       => PageHooks.addHook(hook)
      case hook: LayoutHook     => LayoutHooks.addHook(hook)
      case hook: SiteHook       => SiteHooks.addHook(hook)
      case null                 => ()

  def addHooks(hooks: Hook*) =
    hooks foreach { addHook(_) }

  def join[A <: Hook](fst: List[A], snd: List[A]): List[A] =
    (fst ++ snd).sorted

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
  def apply(fileType: String, extensions: String, outputExt: String): Unit

trait ConverterBeforeConvert extends ConverterHook:
  def apply(str: String, filepath: String): Unit

trait ConverterAfterConvert extends ConverterHook:
  def apply(str: String): Unit

object ConverterHooks:

  private val _beforeInits = ListBuffer[ConverterBeforeInit]()
  private val _beforeConverts = ListBuffer[ConverterBeforeConvert]()
  private val _afterConverts = ListBuffer[ConverterAfterConvert]()

  def addHook(hook: ConverterHook) =
    hook match
      case hook: ConverterBeforeInit    => _beforeInits += hook
      case hook: ConverterBeforeConvert => _beforeConverts += hook
      case hook: ConverterAfterConvert  => _afterConverts += hook
      case null                         => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeConverts = _beforeConverts.toList.sorted
  def afterConverts = _afterConverts.toList.sorted

sealed trait CollectionHook extends Hook

trait CollectionBeforeInit extends CollectionHook with BeforeInit
trait CollectionBeforeLocals extends CollectionHook with BeforeLocals
trait CollectionBeforeRender extends CollectionHook with BeforeRender
trait CollectionAfterRender extends CollectionHook with AfterRender
trait CollectionAfterWrite extends CollectionHook with AfterWrite[Collection]

object CollectionHooks:

  private val _beforeInits = ListBuffer[CollectionBeforeInit]()
  private val _beforeLocals = ListBuffer[CollectionBeforeLocals]()
  private val _beforeRenders = ListBuffer[CollectionBeforeRender]()
  private val _afterRenders = ListBuffer[CollectionAfterRender]()
  private val _afterWrites = ListBuffer[CollectionAfterWrite]()

  def addHook(hook: CollectionHook) =
    hook match
      case hook: CollectionBeforeInit   => _beforeInits += hook
      case hook: CollectionBeforeLocals => _beforeLocals += hook
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
  def apply(context: IObj, contentPartial: String = ""): Unit

trait LayoutAfterRender extends LayoutHook with AfterRender

object LayoutHooks:

  private val _beforeInits = ListBuffer[LayoutBeforeInit]()
  private val _beforeRenders = ListBuffer[LayoutBeforeRender]()
  private val _afterRenders = ListBuffer[LayoutAfterRender]()

  def addHook(hook: LayoutHook) =
    hook match
      case hook: LayoutBeforeInit   => _beforeInits += hook
      case hook: LayoutBeforeRender => _beforeRenders += hook
      case hook: LayoutAfterRender  => _afterRenders += hook
      case null                     => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeRenders = _beforeRenders.toList.sorted
  def afterRenders = _afterRenders.toList.sorted

sealed trait SiteHook extends Hook

trait SiteBeforeInit extends SiteHook with BeforeInit
trait SiteBeforeLocals extends SiteHook with BeforeLocals
trait SiteBeforeRender extends SiteHook with BeforeRender
trait SiteAfterRender extends SiteHook with AfterRender
trait SiteAfterWrite extends SiteHook with AfterWrite[Site]

object SiteHooks:

  private val _beforeInits = ListBuffer[SiteBeforeInit]()
  private val _beforeLocals = ListBuffer[SiteBeforeLocals]()
  private val _beforeRenders = ListBuffer[SiteBeforeRender]()
  private val _afterRenders = ListBuffer[SiteAfterRender]()
  private val _afterWrites = ListBuffer[SiteAfterWrite]()

  def addHook(hook: SiteHook) =
    hook match
      case hook: SiteBeforeInit   => _beforeInits += hook
      case hook: SiteBeforeLocals => _beforeLocals += hook
      case hook: SiteBeforeRender => _beforeRenders += hook
      case hook: SiteAfterRender  => _afterRenders += hook
      case hook: SiteAfterWrite   => _afterWrites += hook
      case null                   => ()

  def beforeInits = _beforeInits.toList.sorted
  def beforeLocals = _beforeLocals.toList.sorted
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

  def addHook(hook: ReaderHook) =
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

  def addHook(hook: PostHook) =
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

  def addHook(hook: PageHook) =
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
