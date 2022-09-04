package com.anglypascal.scalite.utils

import com.anglypascal.scalite.utils.StringProcessors.pad
import com.anglypascal.scalite.utils.Colors.*
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.LayoutBase

class LoggingLayout extends LayoutBase[ILoggingEvent]:
  def doLayout(event: ILoggingEvent): String =
    val sbuf = new StringBuffer(128)
    sbuf.append(
      pad(event.getTimeStamp - event.getLoggerContextVO.getBirthTime, 4)
    )
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
