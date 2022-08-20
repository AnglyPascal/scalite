package com.anglypascal.scalite

/** Provides classes for Collections of sort. A Collection holds some elements
  * of the Item class, and is responsible for rendering the items.
  *
  * ==Overview of Item trait==
  *
  * [[com.anglypascal.scalite.collections.Item]] defines an Item of a
  * Collection. It has an internal state defined in a DObj `locals` and a render
  * method thar returns the processed contents of this Item.
  *
  * [[com.anglypascal.scalite.collections.Post]] is a predefined Item. It
  * represents a blog post that has a source file in some markup language and
  * whose contents will be rendered with a template Layout.
  *
  * [[com.anglypascal.scalite.collections.Draft]] is a predefined Item. It
  * represents a draft blog post. It's a subclass of Post which is only rendered
  * when drafts are configured to be rendered.
  *
  * [[com.anglypascal.scalite.collections.GenericItem]] is a basic
  * implementation of Item. This is the default Item class that is used when a
  * new collection is created but for which an implementation hasn't been
  * provided.
  *
  * ==Overview of Collection trait==
  *
  * [[com.anglypascal.scalite.collections.Collection]] defines a name of the
  * collection, a collection of items, a method apply to collect and prepare all
  * of its items, and a process method to render and write all of its items to
  * the disk
  *
  * [[com.anglypascal.scalite.collections.Posts]] is a predefined colleciton for
  * Post objects.
  *
  * [[com.anglypascal.scalite.collections.Drafts]] is a predefined colleciton
  * for Draft objects.
  *
  * [[com.anglypascal.scalite.collections.GenericCollection]] is a predefined
  * colleciton for GenericItem objects. This is the default collection for use
  * when a new collection is created which doesn't have an implementation.
  *
  * ==Collections object==
  *
  * The standalone object Collections holds references to all defined
  * implementation to Collection objects, and is responsible for initiating the
  * Collection objects, and rendering them.
  */

package object collections {}
