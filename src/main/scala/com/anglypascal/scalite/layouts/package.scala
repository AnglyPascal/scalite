package com.anglypascal.layouts

/** Package providing classes for templates. Templates are known as Layouts.
  *
  * ==Overview==
  *
  * [[com.anglypascal.scalite.documents.Layout]] gives the trait for a template.
  * This layout can be in any templating language, and only defines two methods:
  * `render`, which given a [[com.anglypascal.scalite.data.DObj]] will render
  * the contents of the template using the values of the DObj, and `parent`,
  * which defines the parent Layout of this one. The parent is set in the
  * front_matter of layout file under the layout option.
  *
  * [[com.anglypascal.scalite.documents.Layouts]] standalone object that
  * provides methods to initiate all the Layout objects from a given layouts
  * directory using the provided implementations of LayoutObject.
  *
  * [[com.anglypascal.scalite.documents.LayoutObject]] implements a LayoutObject
  * plugin that allows the support for new Layout implementations.
  *
  * ==Providing new implementations==
  *
  * To provide support for a new templating language, a user must
  *   - provide an implementation of the Layout trait, that implements the
  *     internal logics of the templating engine, and
  *   - provide an implementation of the LayoutObject trait that fetches all the
  *     available templates written in this language from a given directory
  */

package object layouts {}
