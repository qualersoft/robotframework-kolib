plugins {
  `java-library`
}

dependencies {
  implementation(kotlin("reflect"))
  // todo check if requied
  compileOnly(group = "org.robotframework", name = "robotframework")
}