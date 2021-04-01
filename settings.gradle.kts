pluginManagement {
  val kotlinVersion = "1.4.10"
  repositories {
    gradlePluginPortal()
    jcenter()
    mavenCentral()
  }
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("io.spring.dependency-management") version "1.0.10.RELEASE"

    // quality
    id("org.sonarqube") version "3.1.1"

    // documentation
    id("org.jetbrains.dokka") version "1.4.10.2"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
  }
}

include(":robotframework-kolib-core", ":robotframework-kolib-spring")
