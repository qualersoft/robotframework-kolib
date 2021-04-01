plugins {
  kotlin("jvm")
  id("org.jetbrains.dokka")

  id("io.spring.dependency-management")

  `java-library`
  `maven-publish`
}

dependencies {
  api(project(":robotframework-kolib-core"))

  //add groovy to allow spring bean definition in groovy-style
  compileOnly(group = "org.codehaus.groovy", name = "groovy")

  implementation(kotlin("reflect"))
  implementation(group = "org.springframework", name = "spring-context")

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }
}