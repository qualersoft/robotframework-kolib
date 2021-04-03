import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.dsl.DependencySetHandler
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  base

  kotlin("jvm") apply false
  kotlin("plugin.spring") apply false
  id("org.jetbrains.dokka") apply false

  id("io.spring.dependency-management") apply false

  `java-library`
  `maven-publish`
}

allprojects {

  group = "de.qualersoft.robotframework"
  version = "0.0.1-SNAPSHOT"

  repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
  }
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")
  apply(plugin = "io.spring.dependency-management")
  apply(plugin = "org.jetbrains.dokka")
  apply(plugin = "maven-publish")

  the<DependencyManagementExtension>().apply {
    dependencies {
      fun dependency(group: String, name: String, version: String) = this.dependency(
        mapOf("group" to group, "name" to name, "version" to version)
      )

      fun dependencySet(group: String, version: String, action: ((DependencySetHandler).() -> Unit)) =
        dependencySet(mapOf("group" to group, "version" to version), action)

      dependencySet(group = "io.kotest", version = "4.3.2") {
        entry("kotest-runner-junit5-jvm")
        entry("kotest-assertions-core-jvm")
        entry("kotest-property-jvm")
      }

      dependency(group = "org.robotframework", name = "robotframework", version = "3.2.2")

      dependencySet(group = "org.springframework.boot", version = "2.4.4") {
        entry("spring-boot-starter")
      }

      //add groovy to allow spring bean definition in groovy-style
      dependency(group = "org.codehaus.groovy", name = "groovy", version = "3.0.7")
    }
  }

  dependencies {

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
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