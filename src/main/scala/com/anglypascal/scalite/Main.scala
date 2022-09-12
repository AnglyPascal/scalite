package com.anglypascal.scalite

import com.anglypascal.scalite.utils.StringProcessors.quote

@main
def main =

  val str = """
[1] hello
[2] world
some text with [a][2]

[3] bye bye
  """

  val regex = """ {0,3}(?:(\[[^\]]+\])(.+))""".r

  for m <- regex.findAllMatchIn(str) do 
    println(m.toString())
