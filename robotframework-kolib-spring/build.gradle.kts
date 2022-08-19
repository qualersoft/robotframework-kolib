plugins {
  kotlin("jvm")

  id("io.spring.dependency-management")

  `java-library`
}

dependencies {
  api(project(":robotframework-kolib-core"))

  // add groovy to allow spring bean definition in groovy-style
  compileOnly(group = "org.codehaus.groovy", name = "groovy")

  implementation(kotlin("reflect"))

  implementation(group = "org.springframework.boot", name = "spring-boot-starter-logging")
  implementation(group = "jakarta.annotation", name = "jakarta.annotation-api", version = "2.1.1")
  api(group = "org.springframework.boot", name = "spring-boot")

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }

  testImplementation(group = "org.yaml", name = "snakeyaml", version = "1.30")

  testImplementation(group = "ch.qos.logback", name = "logback-classic")
  testImplementation(group = "javax.annotation", name = "javax.annotation-api", version = "1.3.2")
  testImplementation(group = "org.robotframework", name = "robotframework")
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
