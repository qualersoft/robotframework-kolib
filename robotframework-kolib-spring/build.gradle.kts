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
  //api(group = "org.yaml", name = "snakeyaml", version = "1.27")

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }
}