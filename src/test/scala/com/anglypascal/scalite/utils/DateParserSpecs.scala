package com.anglypascal.scalite.utils

import org.scalatest.flatspec.AnyFlatSpec

class DateParserSpecs extends AnyFlatSpec:
  import DateParser.*

  it should "parse date properly" in {
    val dateStr = "2022-08-28 12:00:45.291"
    val dateFmt = "dd MMM, yyyy HH:mm:ss"

    val date = dateParseObj(dateStr, dateFmt)
    assert(
      date("dateString").getStr.get === "28 Aug, 2022 12:00:45" &&
        date("year").getStr.get === "2022" &&
        date("shortYear").getStr.get === "22" &&
        date("month").getStr.get === "08" &&
        date("iMonth").getStr.get === "8" &&
        date("shortMonth").getStr.get === "Aug" &&
        date("longMonth").getStr.get === "August" &&
        date("day").getStr.get === "28" &&
        date("iDay").getStr.get === "28" &&
        date("yDay").getStr.get === "240" &&
        date("wDay").getStr.get === "7" &&
        date("shortDay").getStr.get === "Sun" &&
        date("longDay").getStr.get === "Sunday" &&
        date("week").getStr.get === "34" &&
        date("wYear").getStr.get === "34" &&
        date("hour").getStr.get === "12" &&
        date("minute").getStr.get === "00" &&
        date("second").getStr.get === "45"
    )
  }

  it should "parse date without millies properly" in {
    val dateStr = "2022-08-28 12:00:45"
    val dateFmt = "dd MMM, yyyy HH:mm:ss"

    val date = dateParseObj(dateStr, dateFmt)
    assert(
      date("dateString").getStr.get === "28 Aug, 2022 12:00:45" &&
        date("year").getStr.get === "2022" &&
        date("shortYear").getStr.get === "22" &&
        date("month").getStr.get === "08" &&
        date("iMonth").getStr.get === "8" &&
        date("shortMonth").getStr.get === "Aug" &&
        date("longMonth").getStr.get === "August" &&
        date("day").getStr.get === "28" &&
        date("iDay").getStr.get === "28" &&
        date("yDay").getStr.get === "240" &&
        date("wDay").getStr.get === "7" &&
        date("shortDay").getStr.get === "Sun" &&
        date("longDay").getStr.get === "Sunday" &&
        date("week").getStr.get === "34" &&
        date("wYear").getStr.get === "34" &&
        date("hour").getStr.get === "12" &&
        date("minute").getStr.get === "00" &&
        date("second").getStr.get === "45"
    )
  }

  it should "parse date without time properly" in {
    val dateStr = "2022-08-28"
    val dateFmt = "dd MMM, yyyy HH:mm:ss"

    val date = dateParseObj(dateStr, dateFmt)
    assert(
      date("dateString").getStr.get === "28 Aug, 2022 00:00:00" &&
        date("year").getStr.get === "2022" &&
        date("shortYear").getStr.get === "22" &&
        date("month").getStr.get === "08" &&
        date("iMonth").getStr.get === "8" &&
        date("shortMonth").getStr.get === "Aug" &&
        date("longMonth").getStr.get === "August" &&
        date("day").getStr.get === "28" &&
        date("iDay").getStr.get === "28" &&
        date("yDay").getStr.get === "240" &&
        date("wDay").getStr.get === "7" &&
        date("shortDay").getStr.get === "Sun" &&
        date("longDay").getStr.get === "Sunday" &&
        date("week").getStr.get === "34" &&
        date("wYear").getStr.get === "34" &&
        date("hour").getStr.get === "00" &&
        date("minute").getStr.get === "00" &&
        date("second").getStr.get === "00"
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
