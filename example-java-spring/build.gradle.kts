import de.qualersoft.robotframework.gradleplugin.tasks.BasicRobotFrameworkTask
import de.qualersoft.robotframework.gradleplugin.tasks.LibdocTask
import de.qualersoft.robotframework.gradleplugin.tasks.RunRobotTask
import de.qualersoft.robotframework.gradleplugin.tasks.TestdocTask

plugins {
  java
  id("de.qualersoft.robotframework") version "0.0.4"
}

val javaVersion = JavaVersion.VERSION_21
java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

robotframework {
  robotVersion {
    version = "4.1.2"
  }
  val reports = project.layout.buildDirectory.dir("robot-reports").get()
  robot {
    outputDir.set(reports)
  }
}

repositories {
  mavenCentral()
}

val rfSrcRoot = "src/test/robots"
sourceSets {
  java {
    test {
      resources {
        srcDirs(rfSrcRoot)
      }
    }
  }
}

dependencies {
  implementation(group = "jakarta.inject", name = "jakarta.inject-api")
  implementation(group = "com.github.spotbugs", name = "spotbugs-annotations")

  implementation(group = "ch.qos.logback", name = "logback-classic")

  implementation(project(":example-core"))
  implementation(project(":robotframework-kolib-spring"))

  implementation(group = "org.yaml", name = "snakeyaml", version = "2.2")

  implementation(group = "org.assertj", name = "assertj-core", version = "3.26.3")
}

tasks {
  withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
  }

  val libName = "JExampleLib"
  register<LibdocTask>("libdoc${libName}Html") {
    dependsOn(jar)
    libdoc {
      outputDirectory.set(layout.buildDirectory.dir("doc/libdoc/lib"))
      outputFile.set(file("$libName.html"))
      libraryOrResourceFile = libName
    }
  }
  register<LibdocTask>("libdoc${libName}Libsepc") {
    dependsOn(jar)
    libdoc {
      outputDirectory.set(layout.buildDirectory.dir("doc/libdoc/lib"))
      outputFile.set(file("$libName.libspec"))

      libraryOrResourceFile = libName
    }
  }

  register<TestdocTask>("testdocDemo") {
    sources = files("$rfSrcRoot/suites")
  }

  register<RunRobotTask>("debug") {
    robot {
      include = mutableListOf("Debug")
    }
    sources = files("$rfSrcRoot/suites")
  }
  register<RunRobotTask>("runTests") {
    sources = files("$rfSrcRoot/suites")
  }
  withType<RunRobotTask>().configureEach {
    dependsOn(jar)
    group = "robot"
    outputs.upToDateWhen {
      false
    }
  }

  withType<BasicRobotFrameworkTask>().configureEach {
    jvmArgs(
      listOf(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED",
        "-Dpython.console.encoding=UTF-8"
      )
    )
  }
}
