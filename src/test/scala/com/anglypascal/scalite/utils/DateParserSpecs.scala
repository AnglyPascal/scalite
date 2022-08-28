package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec

class DateParserSpecs extends AnyFlatSpec:
  import DateParser.*

  it should "parse date properly" in {
    val dateStr = "2022-08-28 12:00:45.291"
    val dateFmt = "dd MMM, yyyy HH:mm:ss"

    val date = dateParseObj(dateStr, dateFmt)
    assert(
      date("dateString").str === "28 Aug, 2022 12:00:45" &&
        date("year").str === "2022" &&
        date("shortYear").str === "22" &&
        date("month").str === "08" &&
        date("iMonth").str === "8" &&
        date("shortMonth").str === "Aug" &&
        date("longMonth").str === "August" &&
        date("day").str === "28" &&
        date("iDay").str === "28" &&
        date("yDay").str === "240" &&
        date("wDay").str === "7" &&
        date("shortDay").str === "Sun" &&
        date("longDay").str === "Sunday" &&
        date("week").str === "34" &&
        date("wYear").str === "34" &&
        date("hour").str === "12" &&
        date("minute").str === "00" &&
        date("second").str === "45"
    )
  }

  it should "parse date without millies properly" in {
    val dateStr = "2022-08-28 12:00:45"
    val dateFmt = "dd MMM, yyyy HH:mm:ss"

    val date = dateParseObj(dateStr, dateFmt)
    assert(
      date("dateString").str === "28 Aug, 2022 12:00:45" &&
        date("year").str === "2022" &&
        date("shortYear").str === "22" &&
        date("month").str === "08" &&
        date("iMonth").str === "8" &&
        date("shortMonth").str === "Aug" &&
        date("longMonth").str === "August" &&
        date("day").str === "28" &&
        date("iDay").str === "28" &&
        date("yDay").str === "240" &&
        date("wDay").str === "7" &&
        date("shortDay").str === "Sun" &&
        date("longDay").str === "Sunday" &&
        date("week").str === "34" &&
        date("wYear").str === "34" &&
        date("hour").str === "12" &&
        date("minute").str === "00" &&
        date("second").str === "45"
    )
  }

  it should "parse date without time properly" in {
    val dateStr = "2022-08-28"
    val dateFmt = "dd MMM, yyyy HH:mm:ss"

    val date = dateParseObj(dateStr, dateFmt)
    assert(
      date("dateString").str === "28 Aug, 2022 00:00:00" &&
        date("year").str === "2022" &&
        date("shortYear").str === "22" &&
        date("month").str === "08" &&
        date("iMonth").str === "8" &&
        date("shortMonth").str === "Aug" &&
        date("longMonth").str === "August" &&
        date("day").str === "28" &&
        date("iDay").str === "28" &&
        date("yDay").str === "240" &&
        date("wDay").str === "7" &&
        date("shortDay").str === "Sun" &&
        date("longDay").str === "Sunday" &&
        date("week").str === "34" &&
        date("wYear").str === "34" &&
        date("hour").str === "00" &&
        date("minute").str === "00" &&
        date("second").str === "00"
    )
  }

  behavior of "lastModifiedTime" 

  it should "read last modified time properly for valid files" in {
    val file = "src/test/resources/dirs/readFrom/1.txt"
    val fmt = "dd MMM, yyyy HH:mm:ss z"
    val modT = lastModifiedTime(file, fmt)
    assert(modT == "27 Aug, 2022 19:49:10 BST")
  }

  it should "fail for invalid files" in {
    val file = "src/test/resources/dirs/readFrom/0.txt"
    val fmt = "dd MMM, yyyy HH:mm:ss z"
    val modT = lastModifiedTime(file, fmt)
    assert(modT == "")
  }
