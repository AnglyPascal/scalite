package com.anglypascal.scalite.documents

import com.rallyhealth.weejson.v1.Obj

/** Page represents a page of the website.
  *
  * The model used here assumes that each page has content file, typically in
  * markdown format, and one mustache template file.
  *
  * Page only handles the rendering and output because there might be pages that
  * are automatically generated and hence doesn't require a reader.
  *
  * Pages that contain user given content are handled by the Document subtrait
  * of Page.
  */
trait Page:

  /** Specify the parent template name */
  protected val parent_name: String

  /** Make it Option[Layout] and also remove redundancies
   */
  protected var _parent: Layout = null
  def parent = _parent

  /** Method to write the content of the page to the output file. Needs to be
    * abstract.
    *
    * @param filename
    *   path to the output file
    */
  def write(filename: String): Unit = ???

  /** Renders the content of this page, converting the user provided content and
    * rendering mustache. This results in a HTML formatted string holding the
    * content of the page.
    *
    * @param globals
    *   a weejson Obj containing the global values for site. It will also
    *   contain the "content" tag that will be rendered as part of this document
    * @param partials
    *   contains Layouts in the _includes directory that will be used as
    *   mustache partials
    */
  def render(partials: Map[String, Layout]): String

