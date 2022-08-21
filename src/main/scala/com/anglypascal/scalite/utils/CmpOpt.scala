package com.anglypascal.scalite.utils

/** Compare two options */
def cmpOpt[T](
    o1: Option[T],
    o2: Option[T]
)(using ord: Ordering[T]): Int =
  o1 match
    case None =>
      o2 match
        case None    => 0
        case Some(_) => -1
    case Some(a) =>
      o2 match
        case None    => 1
        case Some(b) => ord.compare(a, b)
