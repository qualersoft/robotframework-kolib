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

  api(group = "org.springframework.boot", name = "spring-boot-starter")

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }

  testImplementation(group= "io.github.classgraph", name="classgraph", version="4.8.102")

  testImplementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
  testImplementation(group = "org.robotframework", name = "robotframework", version = "3.2.2")
}