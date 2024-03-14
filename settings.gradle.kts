pluginManagement {
  val kotlinVersion = "1.9.23"
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  plugins {
    // kotlin
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    // spring
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"

    // quality
    id("io.gitlab.arturbosch.detekt") version "1.23.5"

    // documentation
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
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
