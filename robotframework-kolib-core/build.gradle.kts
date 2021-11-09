plugins {
  `java-library`
}

dependencies {
  implementation(kotlin("reflect"))

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }

  testImplementation(platform("org.junit:junit-bom:5.7.1"))
  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter")
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
