plugins {
  `java-library`
}

dependencies {
  implementation(kotlin("reflect"))
  // todo check if requied
  compileOnly(group = "org.robotframework", name = "robotframework")

  listOf("kotest-runner-junit5-jvm", "kotest-assertions-core").forEach {
    testImplementation(group = "io.kotest", name = it)
  }
}