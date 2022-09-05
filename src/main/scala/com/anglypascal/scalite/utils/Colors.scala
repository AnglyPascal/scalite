package com.anglypascal.scalite.utils

/** Provides convenient methods to give colours to strings
 */
object Colors:

  def GREEN(str: String) = Console.GREEN + str + Console.RESET
  def RED(str: String) = Console.RED + str + Console.RESET
  def BLUE(str: String) = Console.BLUE + str + Console.RESET
  def YELLOW(str: String) = Console.YELLOW + str + Console.RESET
  def MAGENTA(str: String) = Console.MAGENTA + str + Console.RESET
  def CYAN(str: String) = Console.CYAN + str + Console.RESET

  def ERROR(str: String) = Console.BOLD + Console.RED + str + Console.RESET
  def WARN(str: String) = Console.BOLD + Console.YELLOW + str + Console.RESET
