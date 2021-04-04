import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.spring.gradle.dependencymanagement.dsl.DependencySetHandler
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  base

  kotlin("jvm") apply false
  kotlin("plugin.spring") apply false
  id("org.jetbrains.dokka") apply false

  id("org.springframework.boot") apply false
  id("io.spring.dependency-management")

  id("io.gitlab.arturbosch.detekt") apply false
  jacoco
  id("org.sonarqube")

  `java-library`
  `maven-publish`

  id("com.github.ben-manes.versions") version "0.38.0"
}

allprojects {
  apply(plugin = "idea")
  apply(plugin = "io.spring.dependency-management")

  group = "de.qualersoft.robotframework"
  version = "0.0.1-SNAPSHOT"

  repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
  }

  dependencyManagement {
    dependencies {
      fun dependency(group: String, name: String, version: String) = this.dependency(
        mapOf("group" to group, "name" to name, "version" to version)
      )

      fun dependencySet(group: String, version: String, action: ((DependencySetHandler).() -> Unit)) =
        dependencySet(mapOf("group" to group, "version" to version), action)

      dependencySet(group = "io.kotest", version = "4.4.3") {
        entry("kotest-runner-junit5-jvm")
        entry("kotest-assertions-core-jvm")
        entry("kotest-property-jvm")
      }

      dependency(group = "org.robotframework", name = "robotframework", version = "3.2.2")

      dependency(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")

      dependencySet(group = "org.springframework.boot", version = "2.4.4") {
        entry("spring-boot-starter")
      }

      //add groovy to allow spring bean definition in groovy-style
      dependency(group = "org.codehaus.groovy", name = "groovy", version = "3.0.7")
    }
  }
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")

  apply(plugin = "jacoco")
  apply(plugin = "io.gitlab.arturbosch.detekt")

  apply(plugin = "org.jetbrains.dokka")

  apply(plugin = "maven-publish")

  dependencies {
    implementation(group = "ch.qos.logback", name = "logback-classic")
  }

  configure<DetektExtension> {
    failFast = true
    config = files("$rootDir/detekt.yml")
    input = files("src/main/kotlin")

    reports {
      html.enabled = true
      xml.enabled = true
      txt.enabled = false
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      apiVersion = "1.4"
    }
  }

  java {
    withSourcesJar()
    withJavadocJar()
  }

  val dokkaJavadoc: DokkaTask by tasks
  dokkaJavadoc.apply {
    outputDirectory.set(tasks.javadoc.get().destinationDir)
  }

  tasks.getByName<Jar>("javadocJar") {
    dependsOn.add(dokkaJavadoc)
  }

  tasks.jar {
    manifest {
      attributes(
        mapOf(
          "Implementation-Title" to (rootProject.name + '.' + project.name),
          "Implementation-Version" to project.version
        )
      )
    }
  }

  tasks.check {
    dependsOn(tasks.withType<JacocoReport>())
  }

  tasks.withType<JacocoReport> {
    reports {
      xml.isEnabled = true
      html.isEnabled = true
      csv.isEnabled = false
    }
  }

  publishing {
    publications {
      create<MavenPublication>("maven") {
        from(components["java"])
        versionMapping {
          usage("java-api") {
            fromResolutionOf("runtimeClasspath")
          }
          usage("java-runtime") {
            fromResolutionResult()
          }
        }
      }
    }
  }
}