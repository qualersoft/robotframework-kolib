import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm")
}

val javaVersion = JavaVersion.VERSION_21
java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(group = "org.springframework.boot", name = "spring-boot")
  implementation(group = "org.springframework", name = "spring-web")

  implementation(group = "org.json", name = "json")
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
  }
}
