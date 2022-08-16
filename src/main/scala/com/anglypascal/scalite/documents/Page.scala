package com.anglypascal.scalite.documents

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
  protected var _parent: Option[Layout] = None
  def parent = _parent

  /** Method to write the content of the page to the output file. Needs to be
    * abstract.
    *
    * @param filepath
    *   path to the output file
    */
  def write(filepath: String): Unit

  /** Renders the content of this page, converting the user provided content and
    * rendering mustache. This results in a HTML formatted string holding the
    * content of the page.
    *
    * @returns 
    *   The html string of this page
    */
  def render: String

