plugins {
  kotlin("jvm")

  id("io.spring.dependency-management")

  `java-library`
}

dependencies {
  api(project(":robotframework-kolib-core"))

  // add groovy to allow spring bean definition in groovy-style
  compileOnly(group = "org.apache.groovy", name = "groovy")

  implementation(kotlin("reflect"))

  implementation(group = "org.springframework.boot", name = "spring-boot-starter-logging")
  implementation(group = "jakarta.inject", name = "jakarta.inject-api")
  implementation(group = "jakarta.annotation", name = "jakarta.annotation-api")
  api(group = "org.springframework.boot", name = "spring-boot")

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }

  testImplementation(group = "org.yaml", name = "snakeyaml", version = "2.2")

  testImplementation(group = "ch.qos.logback", name = "logback-classic")
  testImplementation(group = "org.robotframework", name = "robotframework")
  testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine")
}

tasks.compileTestJava {
  this.options.compilerArgs = listOf("-parameters")
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events = mutableSetOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }

  addTestListener(object : TestListener {
    override fun beforeSuite(suite: TestDescriptor) {}
    override fun beforeTest(testDescriptor: TestDescriptor) {}
    override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
    override fun afterSuite(suite: TestDescriptor, result: TestResult) {
      if (null == suite.parent) { // root suite
        logger.lifecycle("----")
        logger.lifecycle("Test result: ${result.resultType}")
        logger.lifecycle(
          "Test summary: ${result.testCount} tests, " +
            "${result.successfulTestCount} succeeded, " +
            "${result.failedTestCount} failed, " +
            "${result.skippedTestCount} skipped"
        )
      }
    }
  })
}

publishing {
  publications {
    named<MavenPublication>("maven") {
      pom {
        name.set("Kolib spring")
        description.set("Library using spring for keyword discovery.")
      }
    }
  }
}
