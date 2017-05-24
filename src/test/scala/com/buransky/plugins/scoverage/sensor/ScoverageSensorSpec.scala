 /*
 * Sonar Scoverage Plugin
 * Copyright (C) 2013 Rado Buransky
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
 package com.buransky.plugins.scoverage.sensor

 import java.io.File
 import java.util

 import com.buransky.plugins.scoverage.language.Scala
 import com.buransky.plugins.scoverage.measure.ScalaMetrics
 import com.buransky.plugins.scoverage.{CoveredStatement, DirectoryStatementCoverage, FileStatementCoverage, ProjectStatementCoverage, ScoverageReportParser, StatementPosition}
 import org.junit.runner.RunWith
 import org.mockito.Mockito._
 import org.scalatest.junit.JUnitRunner
 import org.scalatest.mock.MockitoSugar
 import org.scalatest.{FlatSpec, Matchers}
 import org.sonar.api.batch.fs.{FilePredicate, FilePredicates, FileSystem}
 import org.sonar.api.config.Settings
 import org.sonar.api.resources.Project
 import org.sonar.api.scan.filesystem.PathResolver

 import scala.collection.JavaConversions._
 import com.buransky.plugins.scoverage.pathcleaner.PathSanitizer
 import org.mockito.Matchers.any
 import org.sonar.api.batch.fs.internal.DefaultInputFile


 @RunWith(classOf[JUnitRunner])
 class ScoverageSensorSpec extends FlatSpec with Matchers with MockitoSugar {
   behavior of "shouldExecuteOnProject"

   it should "succeed for Scala project" in new ShouldExecuteOnProject {
     checkShouldExecuteOnProject(List("scala"), true)
   }

   it should "succeed for mixed projects" in new ShouldExecuteOnProject {
     checkShouldExecuteOnProject(List("scala", "java"), true)
   }

   it should "fail for Java project" in new ShouldExecuteOnProject {
     checkShouldExecuteOnProject(List("java"), false)
   }

   class ShouldExecuteOnProject extends ScoverageSensorScope {
     protected def checkShouldExecuteOnProject(languages: Iterable[String], expectedResult: Boolean) {
       // Setup
       val project = mock[Project]
       when(fileSystem.languages()).thenReturn(new util.TreeSet(languages))

       // Execute & asser
       shouldExecuteOnProject(project) should equal(expectedResult)

       verify(fileSystem, times(1)).languages

     }
   }

   behavior of "analyse for single project"

   it should "set 0% coverage for a project without children" in new AnalyseScoverageSensorScope {
     // Setup
     val pathToScoverageReport = "#path-to-scoverage-report#"
     val reportAbsolutePath = "#report-absolute-path#"
     val projectStatementCoverage =
       ProjectStatementCoverage("project-name", List(
         DirectoryStatementCoverage(File.separator, List(
           DirectoryStatementCoverage("home", List(
             FileStatementCoverage("a.scala", 3, 2, Nil)
           ))
         )),
         DirectoryStatementCoverage("x", List(
           FileStatementCoverage("b.scala", 3, 2,
             Seq(
               CoveredStatement(StatementPosition(1, 1), StatementPosition(1, 2), 0, true),
               CoveredStatement(StatementPosition(1, 1), StatementPosition(1, 2), 1, true),
               CoveredStatement(StatementPosition(2, 2), StatementPosition(2, 3), 1, false)
            )
           )
         ))
       ))
     val reportFile = mock[java.io.File]
     val moduleBaseDir = mock[java.io.File]
     val filePredicates = mock[FilePredicates]
     val inputFile = new DefaultInputFile("project", "a.scala").setLines(2)
     
     when(reportFile.exists).thenReturn(true)
     when(reportFile.isFile).thenReturn(true)
     when(reportFile.getAbsolutePath).thenReturn(reportAbsolutePath)
     when(settings.getString(SCOVERAGE_REPORT_PATH_PROPERTY)).thenReturn(pathToScoverageReport)
     when(fileSystem.baseDir).thenReturn(moduleBaseDir)
     when(fileSystem.predicates).thenReturn(filePredicates)
     when(fileSystem.inputFile(any[FilePredicate]())).thenReturn(inputFile)
     when(pathResolver.relativeFile(moduleBaseDir, pathToScoverageReport)).thenReturn(reportFile)
     when(scoverageReportParser.parse(any[String](), any[PathSanitizer]())).thenReturn(projectStatementCoverage)

     // Execute
     analyse(project, context)

     val metricValues = Map(
       ScalaMetrics.statementCoverage -> 66.66666666666666,
       ScalaMetrics.coveredStatements -> 2.0,
       ScalaMetrics.totalStatements -> 3.0
     )
     metricValues.foreach { case (metric, metricValue) =>
       val measure = context.measures.get(metric.getKey)
       measure.isDefined shouldBe true
       measure.get.value() shouldBe metricValue
     }
     context.coverages.size shouldBe 2
     val actualCoverages = context.coverages.filter(_.conditions() > 0).toArray
     val first = actualCoverages.head
     first.conditions() shouldBe 2
     first.coveredConditions() shouldBe 1
     first.linesToCover() shouldBe 2
     first.coveredLines() shouldBe 1
   }

   class AnalyseScoverageSensorScope extends ScoverageSensorScope {
     val project = mock[Project]
     val context = new TestSensorContext

     override protected lazy val scoverageReportParser = mock[ScoverageReportParser]
     override protected def createPathSanitizer(sonarSources: String) = mock[PathSanitizer]
   }

   class ScoverageSensorScope extends {
     val scala = new Scala
     val settings = mock[Settings]
     val pathResolver = mock[PathResolver]
     val fileSystem = mock[FileSystem]
   } with ScoverageSensor(settings, pathResolver, fileSystem)

 }
