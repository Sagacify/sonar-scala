package com.sagacify.sonar.scala;

import org.sonar.api.config.PropertyDefinition


object ScalaProperties {
  val SCALA_VERSION = "sonar.scala.version"

  val definitions = List(
    PropertyDefinition.builder(SCALA_VERSION)
      .name("version")
      .description("scala version to be used by the ast parser")
      .defaultValue("2.11.8")
      .build()
    )
}
