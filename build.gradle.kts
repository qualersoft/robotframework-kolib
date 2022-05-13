import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.spring.gradle.dependencymanagement.dsl.DependencySetHandler
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
  java

  kotlin("jvm") apply false
  kotlin("plugin.spring") apply false

  id("org.springframework.boot") apply false
  id("io.spring.dependency-management")

  id("io.gitlab.arturbosch.detekt") apply false
  jacoco

  `maven-publish`

  id("com.github.ben-manes.versions") version "0.38.0"

  id("org.jetbrains.dokka") apply false
  id("org.asciidoctor.jvm.convert")
}

jacoco {
  toolVersion = "0.8.7"
}

allprojects {
  apply(plugin = "idea")
  apply(plugin = "io.spring.dependency-management")

  group = "de.qualersoft.robotframework"

  repositories {
    mavenCentral()
  }

  dependencyManagement {
    dependencies {
      fun dependency(group: String, name: String, version: String) = this.dependency(
        mapOf("group" to group, "name" to name, "version" to version)
      )

      fun dependencySet(group: String, version: String, action: ((DependencySetHandler).() -> Unit)) =
        dependencySet(mapOf("group" to group, "version" to version), action)

      dependency(group = "javax.inject", name = "javax.inject", version = "1")
      dependency(group = "javax.annotation", name = "javax.annotation-api", version = "1.3.2")
      dependency(group = "com.github.spotbugs", name = "spotbugs-annotations", version = "4.7.0")

      dependency(group = "org.json", name = "json", version = "20220320")
      dependencySet(group = "io.kotest", version = "4.6.0") {
        entry("kotest-runner-junit5-jvm")
        entry("kotest-assertions-core-jvm")
        entry("kotest-property-jvm")
      }

      dependency(group = "org.robotframework", name = "robotframework", version = "4.0.1")

      dependency(group = "ch.qos.logback", name = "logback-classic", version = "1.2.11")

      dependency(group = "org.springframework", name = "spring-web", version = "5.3.20")
      dependencySet(group = "org.springframework.boot", version = "2.6.7") {
        entry("spring-boot")
        entry("spring-boot-starter-logging")
      }

      // add groovy to allow spring bean definition in groovy-style
      dependency(group = "org.codehaus.groovy", name = "groovy", version = "3.0.7")
    }
  }
}

subprojects {
  if (name.startsWith("example")) return@subprojects // configure example projects separately

  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")

  apply(plugin = "jacoco")
  apply(plugin = "io.gitlab.arturbosch.detekt")

  apply(plugin = "org.jetbrains.dokka")

  apply(plugin = "maven-publish")

  jacoco {
    toolVersion = "0.8.7"
  }

  dependencies {
    implementation(group = "ch.qos.logback", name = "logback-classic")
  }

  configure<DetektExtension> {
    allRules = true
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
    finalizedBy(tasks.withType<JacocoReport>())
  }

  val javaVersion = JavaVersion.VERSION_11
  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = javaVersion.toString()
      apiVersion = "1.6"
    }
  }

  java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
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
      xml.required.set(true)
      html.required.set(true)
      csv.required.set(false)
    }
  }

  publishing {
    publications {
      repositories {
        maven {
          name = "gh-qualersoft-kolib"
          url = uri("https://maven.pkg.github.com/qualersoft/robotframework-kolib")
          credentials {
            username = (
              project.findProperty("publish.gh.qualersoft.rfkolib.gpr.usr")
                ?: System.getenv("USERNAME")
              )?.toString()
            password = (
              project.findProperty("publish.gh.qualersoft.rfkolib.gpr.key")
                ?: System.getenv("TOKEN")
              )?.toString()
          }
        }
      }
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

tasks.register<JacocoReport>("jacocoRootReport") {
  subprojects.forEach {
    if (it.name.startsWith("example")) return@forEach
    group = "verification"
    val srcDirs = it.sourceSets.main.get().allSource.srcDirs
    additionalSourceDirs.from(srcDirs)
    sourceDirectories.from(srcDirs)
    classDirectories.from(it.sourceSets.main.get().output)
    executionData(it.tasks.withType<Test>())
  }
}

tasks.register("updateVersion") {
  description = """ONLY FOR CI/CD purposes!
    |
    |This task is meant to be used by CI/CD to generate new release versions.
    |Prerequists: a `gradle.properties` next to this build-script must exist.
    |   version must follow semver-schema (<number>.<number.<number>*)
    |Usage:
    |  > ./gradlew updateVersion -PnewVersion="the new version"
  """.trimMargin()

  doLast {
    var newVersion = project.findProperty("newVersion") as String?
      ?: throw IllegalArgumentException(
        "No `newVersion` specified!" +
          " Usage: ./gradlew updateVersion -PnewVersion=<version>"
      )

    if (newVersion.contains("snapshot", true)) {
      val props = Properties()
      props.load(getGradlePropsFile().inputStream())
      val currVersion = (props["version"] as String?)!!.split('.').toMutableList()
      val next = currVersion.last()
        .replace(Regex("[^\\d]+"), "").toInt() + 1
      currVersion[currVersion.lastIndex] = "$next-SNAPSHOT"
      newVersion = currVersion.joinToString(".")
    }

    persistVersion(newVersion)
  }
}

fun getGradlePropsFile(): File {
  val propsFile = files("./gradle.properties").singleFile
  if (!propsFile.exists()) {
    val msg = "This task requires version to be stored in gradle.properties file, which does not exist!"
    throw UnsupportedOperationException(msg)
  }
  return propsFile
}

fun persistVersion(newVersion: String) {
  val propsFile = getGradlePropsFile()
  val props = Properties()
  props.load(propsFile.inputStream())
  props.setProperty("version", newVersion)
  props.store(propsFile.outputStream(), null)
}
