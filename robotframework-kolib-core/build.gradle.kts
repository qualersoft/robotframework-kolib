plugins {
  `java-library`
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
  }
}

dependencies {
  implementation(kotlin("reflect"))
  compileOnly(group = "org.python", name = "jython-slim", version = "2.7.3")

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }

  testImplementation(group = "org.python", name = "jython-slim", version = "2.7.3")
  testImplementation(platform("org.junit:junit-bom:5.11.0"))
  testImplementation(group = "org.junit.jupiter", name = "junit-jupiter")
}

tasks {
  test {
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
}

publishing {
  publications {
    named<MavenPublication>("maven") {
      pom {
        name.set("Kolib core")
        description.set("Base library for binding to robot framework.")
      }
    }
  }
}
