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
    events("passed", "skipped", "failed")
  }
}