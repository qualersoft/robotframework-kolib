pluginManagement {
  val kotlinVersion = "2.0.0"
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  plugins {
    // kotlin
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    // spring
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"

    // quality
    id("io.gitlab.arturbosch.detekt") version "1.23.6"

    // documentation
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.asciidoctor.jvm.convert") version "4.0.2"
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
  }
  rulesMode = RulesMode.FAIL_ON_PROJECT_RULES
}

include(
  ":robotframework-kolib-core", ":robotframework-kolib-spring",
  ":example-core", ":example-kotlin-spring", ":example-java-spring"
)
