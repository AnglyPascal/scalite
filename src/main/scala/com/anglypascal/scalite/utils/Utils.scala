package com.anglypascal.scalite.utils

/** Compare two options */
def cmpOpt[T <: Ordered[T]](o1: Option[T], o2: Option[T]): Int =
  o1 match
    case None =>
      o2 match
        case None    => 0
        case Some(_) => -1
    case Some(a) =>
      o2 match
        case None    => 1
        case Some(b) => a compare b

/** Provides convenient methods to give colours to strings */
object Colors:

  inline def GREEN(inline str: String) = Console.GREEN + str + Console.RESET
  inline def RED(inline str: String) = Console.RED + str + Console.RESET
  inline def BLUE(inline str: String) = Console.BLUE + str + Console.RESET
  inline def YELLOW(inline str: String) = Console.YELLOW + str + Console.RESET
  inline def MAGENTA(inline str: String) = Console.MAGENTA + str + Console.RESET
  inline def CYAN(inline str: String) = Console.CYAN + str + Console.RESET

  inline def ERROR(inline str: String) =
    Console.BOLD + Console.RED + str + Console.RESET
  inline def WARN(inline str: String) =
    Console.BOLD + Console.YELLOW + str + Console.RESET
