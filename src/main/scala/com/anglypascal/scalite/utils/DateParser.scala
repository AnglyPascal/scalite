package com.anglypascal.scalite.utils

import com.github.nscala_time.time.Imports._

def dateParser(str: String): Option[String] =
  val date = raw"(\d{4}-\d{2}-\d{2}).*".r
  str match
    case date(d) =>
      val date = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(d)
      Some(date.toString("dd MMM, yyyy"))
    case _ => None

// val test = "2016-05-20-title.md"

/** TODO: This should be stored in the page actually, not just in post. How do
  * we refactor the code to allow this?
  *
  * TODO: add more options to give flexibility of date formats. The
  * customization should be provided in the _config.yml under the date section.
  */
def dateObj(str: String): Map[String, String] =
  val date = raw"(\d{4}-\d{2}-\d{2}).*".r
  str match
    case date(d) =>
      val date = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(d)
      Map(
        "date_string" -> formatDate(date, "dd MMM, yyyy"),
        "day" -> date.day.get.toString,
        "month" -> date.month.getAsText,
        // "i_month" ->
        "year" -> date.year.getAsText,
        "short_year" -> date.year.getAsShortText
      )
    /** TODO: wait these should also be modifiable? Dammit
      */
    case _ => Map()

/** TODO: can be speficied in the _config.yml under the `output_date` tag
  */
def formatDate(date: DateTime, format: String): String =
  date.toString(format)
