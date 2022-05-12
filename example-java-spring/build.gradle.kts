import de.qualersoft.robotframework.gradleplugin.tasks.BasicRobotFrameworkTask
import de.qualersoft.robotframework.gradleplugin.tasks.LibdocTask
import de.qualersoft.robotframework.gradleplugin.tasks.RunRobotTask
import de.qualersoft.robotframework.gradleplugin.tasks.TestdocTask

plugins {
  java
  id("de.qualersoft.robotframework") version "0.0.4"
}

val javaVersion = JavaVersion.VERSION_17
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

sourceSets {
  java {
    test {
      resources {
        srcDirs("src/test/robots")
      }
    }
  }
}

dependencies {
  implementation(group = "javax.annotation", name = "javax.annotation-api")
  implementation(group = "com.github.spotbugs", name = "spotbugs-annotations")

  implementation(group = "ch.qos.logback", name = "logback-classic")

  implementation(project(":example-core"))
  implementation(project(":robotframework-kolib-spring"))

  implementation(group = "org.yaml", name = "snakeyaml", version = "1.30")

  implementation(group = "org.assertj", name = "assertj-core", version = "3.22.0")
}

tasks {
  withType<JavaCompile>().configureEach { 
    options.compilerArgs.add("-parameters")
  }
  
  val libName = "JExampleLib"
  register<LibdocTask>("libdoc${libName}Html") {
    dependsOn(jar)
    libdoc {
      outputDirectory.set(buildDir.resolve("doc/libdoc/lib"))
      outputFile.set(file("${libName}.html"))
      libraryOrResourceFile = libName
    }
  }
  register<LibdocTask>("libdoc${libName}Libsepc") {
    dependsOn(jar)
    libdoc {
      outputDirectory.set(buildDir.resolve("doc/libdoc/lib"))
      outputFile.set(file("${libName}.libspec"))

      libraryOrResourceFile = libName
    }
  }

  register<TestdocTask>("testdocDemo") {
    sources = files("src/test/robots/suites")
  }

  register<RunRobotTask>("debug") {
    robot {
      include = mutableListOf("Debug")
    }
    sources = files("src/test/robots/suites")
  }
  register<RunRobotTask>("runTests") {
    sources = files("src/test/robots/suites")
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
