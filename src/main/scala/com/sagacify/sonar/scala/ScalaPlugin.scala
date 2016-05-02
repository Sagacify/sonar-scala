package com.sagacify.sonar.scala

import collection.JavaConverters._
import scala.collection.mutable.ListBuffer

import com.buransky.plugins.scoverage.measure.ScalaMetrics
import com.buransky.plugins.scoverage.sensor.ScoverageSensor
import com.buransky.plugins.scoverage.widget.ScoverageWidget
import com.ncredinburgh.sonar.scalastyle.ScalastyleQualityProfile
import com.ncredinburgh.sonar.scalastyle.ScalastyleRepository
import com.ncredinburgh.sonar.scalastyle.ScalastyleSensor
import org.sonar.api.config.Settings
import org.sonar.api.Extension
import org.sonar.api.resources.AbstractLanguage
import org.sonar.api.SonarPlugin
import scalariform.lexer.ScalaLexer
import scalariform.lexer.Token


/**
 * Defines Scala as a language for SonarQube.
 */
class Scala(val settings: Settings) extends AbstractLanguage("scala", "Scala") {

  import Scala._

  override def getFileSuffixes: Array[String] = Array("scala")

}

object Scala {

  def tokenize(sourceCode: String, scalaVersion: String): List[Token] =
    ScalaLexer.createRawLexer(sourceCode, false, scalaVersion).toList
}

/**
 * Plugin entry point.
 */
class ScalaPlugin extends SonarPlugin {

  override def getExtensions: java.util.List[_] =
     (List(
      classOf[Scala],
      classOf[ScalaSensor],
      classOf[ScalastyleRepository],
      classOf[ScalastyleQualityProfile],
      classOf[ScalastyleSensor],
      classOf[ScalaMetrics],
      classOf[ScoverageSensor],
      classOf[ScoverageWidget]
    ) ++ ScalaProperties.definitions).asJava

  override val toString = getClass.getSimpleName

}
