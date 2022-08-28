package com.anglypascal.scalite.utils

import com.github.nscala_time.time.Imports.*
import com.rallyhealth.weejson.v1.Obj
import com.typesafe.scalalogging.Logger

/** Provides methods for parsing date from string and modified date from file
  *
  * TODO: Provide support for timezones. Take a timezone variable as input in
  * the configs to decide the global timezone stuff
  */
object DateParser:
  import com.anglypascal.scalite.data.DataExtensions.*

  def dateParseObj(dateString: String, dateFormat: String): Obj =
    val logger = Logger("Date parser")
    val reg1 = raw"(\d{4}-\d{2}-\d{2}\s*\d{2}:\d{2}:\d{2}\.\d{3})".r
    val reg2 = raw"(\d{4}-\d{2}-\d{2}\s*\d{2}:\d{2}:\d{2})".r
    val reg3 = raw"(\d{4}-\d{2}-\d{2})".r
    val fmt1 = "yyyy-MM-dd HH:mm:ss.SSS"
    val fmt2 = "yyyy-MM-dd HH:mm:ss"
    val fmt3 = "yyyy-MM-dd"

    try
      val dt: Option[DateTime] =
        dateString match
          case reg1(d) =>
            logger.warn("The date and time was parsed successfully.")
            Some(DateTimeFormat.forPattern(fmt1).parseDateTime(d))
          case reg2(d) =>
            logger.warn(
              "The date was parsed successfully wthout the millisecond."
            )
            Some(DateTimeFormat.forPattern(fmt2).parseDateTime(d))
          case reg3(d) =>
            logger.warn("The date was parsed successfully wthout the time.")
            Some(DateTimeFormat.forPattern(fmt3).parseDateTime(d))
          case _ =>
            logger.warn(
              "Parsing failed because " +
                s"the date string \"$dateString\" did not have valid format"
            )
            None

      dt match
        case Some(d) =>
          dateToObj(d, dateFormat)
        case None => Obj()
    catch
      case e =>
        logger.error(s"${e.toString} thrown while parsing date")
        Obj()

  private def dateToObj(date: DateTime, dateFormat: String): Obj =
    val month = date.month
    Obj(
      "dateString" -> dateToString(date, dateFormat),
      // year
      "year" -> date.year.get.toString,
      "shortYear" -> (date.year.get % 100).toString,
      // month
      "month" -> "%02d".format(month.get),
      "iMonth" -> month.get.toString,
      "shortMonth" -> month.getAsShortText,
      "longMonth" -> month.getAsText,
      // day
      "day" -> "%02d".format(date.day.get),
      "iDay" -> date.day.get.toString,
      "yDay" -> date.dayOfYear.getAsString,
      "wDay" -> date.dayOfWeek.getAsString,
      "shortDay" -> date.dayOfWeek.getAsShortText,
      "longDay" -> date.dayOfWeek.getAsText,
      // week
      "week" -> "%02d".format(date.week.get),
      "wYear" -> date.weekOfWeekyear.getAsString,
      // time
      "hour" -> "%02d".format(date.hour.get),
      "minute" -> "%02d".format(date.minute.get),
      "second" -> "%02d".format(date.second.get)
    )

  private def dateToString(date: DateTime, dateFormat: String): String =
    try date.toString(dateFormat)
    catch
      case e =>
        Logger("dateToString").error(
          s"Didn't receive a valid format string for date ${date.toString}, " +
            s"Given format: $dateFormat"
        )
        ""

  /** Returned the last modified time of the filepath in the given dateFormat */
  def lastModifiedTime(filepath: String, dateFormat: String): String =
    import java.nio.file.Files
    import java.nio.file.Paths

    try
      val path = Paths.get(filepath)
      val modTime = Files.getLastModifiedTime(path).toInstant.toEpochMilli()
      val date = new DateTime(modTime)

      dateToString(date, dateFormat)
    catch
      case e =>
        logger.error(
          s"${e.toString} thrown while getting the last modified time of $filepath"
        )
        ""
