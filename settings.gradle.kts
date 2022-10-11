pluginManagement {
  val kotlinVersion = "1.7.20"
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  plugins {
    // kotlin
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    // spring
    id("org.springframework.boot") version "2.7.4"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"

    // quality
    id("io.gitlab.arturbosch.detekt") version "1.21.0"

    // documentation
    id("org.jetbrains.dokka") version "1.7.20"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
  }
}

include(
  ":robotframework-kolib-core", ":robotframework-kolib-spring",
  ":example-core", ":example-kotlin-spring", ":example-java-spring"
)
