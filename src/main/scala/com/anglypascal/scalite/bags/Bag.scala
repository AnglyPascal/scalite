package com.anglypascal.scalite.bags

import com.anglypascal.scalite.documents.Post

import scala.collection.mutable.{LinkedHashMap, Set}
import com.rallyhealth.weejson.v1.Obj

/** Creates a new type of Bag. Needs the implementation of addToBags which
  * defines how a post is added to the Bag.
  *
  * Objects of this trait adds themselves to the set of available bags in the
  * Bag object.
  */
trait Bag(ctype: String):

  /** Underlying PostsBag class which will take care of generating individual
    * page for the bags of this type.
    *
    * @constructor
    *   Create a new element of this bag type
    * @param name
    *   name of this element
    * @param globals
    *   a weejson obj containing the global options for this site
    */
  abstract class BagType(name: String, globals: Obj)
      extends PostsBag(ctype, name, globals)

  /** Defines how posts add themselves to this bag type. Usually it's by a
    * combination of specifying bag names in the front matter as a string or
    * list, or by indiciating bag names in the filename or inside a class
    * variable.
    *
    * @param post
    *   A post object to be added to the bags of this type
    * @param globals
    *   a weejson obj containing the global options for this site
    */
  def addToBags(post: Post, globals: Obj): Unit

  // add this Bag to Bag.availableBags
  Bag.addNewBag(this)

/** Companion object that holds all the Bags defined for this website. By
  * default these are Tag and Category. New bags can be added by creating a
  * object of the trait Bag.
  */
object Bag:

  /** Set of the available Bags for this site */
  val availableBags: Set[Bag] = Set()

  /** Add a new Bag to this site */
  def addNewBag(bag: Bag) = availableBags += bag
