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

 import java.lang.Double
 import java.{io, util}

 import org.sonar.api.batch.fs.{FileSystem, InputFile, InputPath}
 import org.sonar.api.batch.rule.ActiveRules
 import org.sonar.api.batch.sensor.coverage.NewCoverage
 import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage
 import org.sonar.api.batch.sensor.highlighting.NewHighlighting
 import org.sonar.api.batch.sensor.highlighting.internal.DefaultHighlighting
 import org.sonar.api.batch.sensor.internal.SensorStorage
 import org.sonar.api.batch.sensor.issue.{Issue, NewIssue}
 import org.sonar.api.batch.sensor.measure
 import org.sonar.api.batch.sensor.measure.NewMeasure
 import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure
 import org.sonar.api.batch.{AnalysisMode, SensorContext}
 import org.sonar.api.config.Settings
 import org.sonar.api.design.Dependency
 import org.sonar.api.measures.{Measure, MeasuresFilter, Metric}
 import org.sonar.api.resources.Resource

 import scala.collection.mutable

 class TestSensorContext extends SensorContext {

   val measures = mutable.Map[String, measure.Measure[_ <: io.Serializable]]()
   val coverages = mutable.HashSet[DefaultCoverage]()
   
   private val storage = new SensorStorage() {

     override def store(measureToSave: measure.Measure[_ <: io.Serializable]): Unit = measures.put(measureToSave.metric().key(), measureToSave)

     override def store(issue: Issue): Unit = ???

     override def store(highlighting: DefaultHighlighting): Unit = ???

     override def store(defaultCoverage: DefaultCoverage): Unit = coverages.add(defaultCoverage)
   }

   override def getResource[R <: Resource](reference: R): R = ???

   override def getResource(inputPath: InputPath): Resource = ???

   override def isExcluded(reference: Resource): Boolean = ???

   override def saveDependency(dependency: Dependency): Dependency = ???

   override def getMeasure[G <: io.Serializable](metric: Metric[G]): Measure[G] = ???

   override def getMeasure[G <: io.Serializable](resource: Resource, metric: Metric[G]): Measure[G] = ???

   override def getParent(reference: Resource): Resource = ???

   override def isIndexed(reference: Resource, acceptExcluded: Boolean): Boolean = ???

   override def index(resource: Resource): Boolean = ???

   override def index(resource: Resource, parentReference: Resource): Boolean = ???

   override def getMeasures[M](filter: MeasuresFilter[M]): M = ???

   override def getMeasures[M](resource: Resource, filter: MeasuresFilter[M]): M = ???

   override def getChildren(reference: Resource): util.Collection[Resource] = ???

   override def saveMeasure(measure: Measure[_ <: io.Serializable]): Measure[_ <: io.Serializable] = ???

   override def saveMeasure(metric: Metric[_ <: io.Serializable], value: Double): Measure[_ <: io.Serializable] = ???

   override def saveMeasure(resource: Resource, metric: Metric[_ <: io.Serializable], value: Double): Measure[_ <: io.Serializable] = ???

   override def saveMeasure(resource: Resource, measure: Measure[_ <: io.Serializable]): Measure[_ <: io.Serializable] = ???

   override def saveMeasure(inputFile: InputFile, metric: Metric[_ <: io.Serializable], value: Double): Measure[_ <: io.Serializable] = ???

   override def saveMeasure(inputFile: InputFile, measure: Measure[_ <: io.Serializable]): Measure[_ <: io.Serializable] = ???

   override def saveSource(reference: Resource, source: String): Unit = ???

   override def saveResource(resource: Resource): String = ???

   override def newHighlighting(): NewHighlighting = ???

   override def activeRules(): ActiveRules = ???

   override def analysisMode(): AnalysisMode = ???

   override def newCoverage(): NewCoverage = new DefaultCoverage(storage)

   override def fileSystem(): FileSystem = ???

   override def settings(): Settings = ???

   override def newIssue(): NewIssue = ???

   override def newMeasure[G <: io.Serializable](): NewMeasure[G] = new DefaultMeasure(storage)
 }
