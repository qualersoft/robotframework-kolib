pluginManagement {
  val kotlinVersion = "1.5.10"
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    // quality
    id("io.gitlab.arturbosch.detekt") version "1.17.1"

    // documentation
    id("org.jetbrains.dokka") version "1.4.30"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
  }
}

include(":robotframework-kolib-core", ":robotframework-kolib-spring")
