package com.anglypascal.scalite.utils

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.LayoutBase
import com.anglypascal.scalite.utils.Colors.*
import com.anglypascal.scalite.utils.StringProcessors.pad
import com.github.nscala_time.time.Imports.*

import java.time.Instant

/** FIXME Checkout what's happening
 */
class LoggingLayout extends LayoutBase[ILoggingEvent]:
  def doLayout(event: ILoggingEvent): String =
    val sbuf = new StringBuffer(128)

    val s = DateTimeZone.forID("Asia/Dhaka")
    val date = new DateTime(s).withZone(s)
    val dateFormat = "HH:mm:ss.SSS"

    sbuf.append(date.toString(dateFormat))
    sbuf.append(" ")
    sbuf.append(level(event))
    sbuf.append(" [")
    sbuf.append(pad(prettyThreadName(event), 15))
    sbuf.append("] ")
    sbuf.append(event.getLoggerName)
    sbuf.append(" - ")
    sbuf.append(event.getFormattedMessage)
    sbuf.append("\n")
    return sbuf.toString()

  def prettyThreadName(event: ILoggingEvent): String =
    val thread = event.getThreadName
    val r = ".*thread-(\\d+)(-.*)?".r
    thread match
      case r(a, b) if b != null => a + ": " + b.split("-").last
      case r(a, b) if b == null => a
      case _                    => thread

  def level(event: ILoggingEvent): String =
    val l = pad(event.getLevel.toString, 5)
    l match
      case "DEBUG" => GREEN(l)
      case "ERROR" => ERROR(l)
      case "WARN " => WARN(l)
      case "TRACE" => BLUE(l)
      case "FATAL" => ERROR(l)
      case _       => l
