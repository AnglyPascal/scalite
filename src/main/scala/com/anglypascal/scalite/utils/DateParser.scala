package com.anglypascal.scalite.utils

import com.github.nscala_time.time.Imports.*
import com.rallyhealth.weejson.v1.Obj

/** TODO: This should be stored in the page actually, not just in post. How do
  * we refactor the code to allow this?
  */
def dateParseObj(dateString: String, dateFormat: String): Obj =
  val date_regex = raw"(\d{4}-\d{2}-\d{2})( \d{2}:\d{2}:\d{2})?.*".r

  val d: Option[DateTime] = dateString match
    case date_regex(d, t) =>
      Some(
        DateTimeFormat
          .forPattern("yyyy-MM-dd HH:mm:ss")
          .parseDateTime(d)
      )
    case date_regex(d) =>
      Some(
        DateTimeFormat
          .forPattern("yyyy-MM-dd")
          .parseDateTime(d)
      )
    // add entry to the log to warn user about date not being found
    case _ => None

  d match
    case Some(d) =>
      dateParseObj(d, dateFormat)
    case None => Obj()

def dateParseObj(date: DateTime, dateFormat: String): Obj =
  Obj(
    "date_string" -> date.toString(dateFormat),
    // year
    "year" -> date.year.getAsText,
    "short_year" -> date.year.getAsShortText,
    // month
    "month" -> "%02d".format(date.month.get),
    "i_month" -> date.month.get.toString,
    "short_month" -> date.month.getAsShortText,
    "long_month" -> date.month.getAsText,
    // day
    "day" -> "%02d".format(date.day.get),
    "i_day" -> date.day.get.toString,
    "y_day" -> date.dayOfYear.getAsString,
    "w_day" -> date.dayOfWeek.getAsString,
    "short_day" -> date.day.getAsShortText,
    "long_day" -> date.day.getAsText,
    // week
    "week" -> "%02d".format(date.week.get),
    "w_day" -> date.weekOfWeekyear.getAsString,
    // time
    "hour" -> "%02d".format(date.hour.get),
    "minute" -> "%02d".format(date.minute.get),
    "second" -> "%02d".format(date.second.get)
  )

def dateToString(date: DateTime, dateFormat: String): String =
  date.toString(dateFormat)
