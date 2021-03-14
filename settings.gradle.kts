pluginManagement {
  val kotlinVersion = "1.4.10"
  repositories {
    gradlePluginPortal()
    jcenter()
    mavenLocal()
    mavenCentral()
  }
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("io.spring.dependency-management") version "1.0.10.RELEASE"

    id("org.jetbrains.dokka") version "1.4.10.2"
  }
}

include(":robotframework-kolib-core", ":robotframework-kolib-spring")
