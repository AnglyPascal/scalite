package com.anglypascal.scalite

import com.anglypascal.scalite.commands.DryRun
import com.anglypascal.scalite.utils.frontMatterParser
import scala.io.Source
import de.larsgrefer.sass.embedded.SassCompilerFactory
import sass.embedded_protocol.EmbeddedSass
import java.io.File
import de.larsgrefer.sass.embedded.importer.ClasspathImporter

import collection.convert.ImplicitConversions.`seq AsJavaList`

@main
def main =
  val sassCompiler = SassCompilerFactory.bundled()
  sassCompiler.setOutputStyle(EmbeddedSass.OutputStyle.COMPRESSED);
  val file = new File("src/main/resources/foo/")
  sassCompiler.setLoadPaths(List(file))

  val css = sassCompiler
    .compileScssString(
      "@import 'baz';"
    )
    .getCss()

  println(css)

  sassCompiler.close();
