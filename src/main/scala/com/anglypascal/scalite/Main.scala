package com.anglypascal.scalite

import com.anglypascal.scalite.commands.DryRun
import com.anglypascal.scalite.utils.frontMatterParser
import scala.io.Source
import de.larsgrefer.sass.embedded.SassCompilerFactory
import sass.embedded_protocol.EmbeddedSass
import java.io.File
import de.larsgrefer.sass.embedded.importer.ClasspathImporter

@main
def main =
  val sassCompiler = SassCompilerFactory.bundled()
  sassCompiler.setOutputStyle(EmbeddedSass.OutputStyle.COMPRESSED);

  val sass = ".foo { .bar { color : #ffffff; @warn 'haha';}}"

  val css = sassCompiler
    .compileFile(new File("src/main/resources/foo/classpathImport.scss"))
    .getCss()

  println(css)

  sassCompiler.close();
