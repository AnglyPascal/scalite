package com.anglypascal.scalite.documents

import com.anglypascal.scalite.layouts.Layouts

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import com.typesafe.scalalogging.Logger

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

  /** Specify the parent template name. Mandate calling it if visible needs to
    * be changed
    */
  protected val parentName: String

  /** Make it Option[Layout] and also remove redundancies */
  protected lazy val parent = Layouts.get(parentName)

  /** Relative permanent link to this page */
  protected lazy val permalink: String

  /** Should this page be rendered and wrote to the disk? */
  lazy val visible: Boolean

  /** The extension of the output file */
  protected lazy val outputExt: String // this will have to be in urlObj, no?

  /** Renders the content of this page, converting the user provided content and
    * rendering mustache. This results in a HTML formatted string holding the
    * content of the page.
    *
    * @returns
    *   The ready to publish content of this page.
    */
  protected lazy val render: String

  /** Method to write the content returned by the render method to the output
    * file at a relative path given by the relative permalink.
    */
  def write(): Unit =
    if !visible then return
    val path =
      if permalink.endsWith(outputExt) then permalink
      else permalink + outputExt
    Logger("Page writer").debug(s"Writing page to $path")
    // Files.write(Paths.get(path), render.getBytes(StandardCharsets.UTF_8))
