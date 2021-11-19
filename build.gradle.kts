import java.util.Properties
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.spring.gradle.dependencymanagement.dsl.DependencySetHandler
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java

  kotlin("jvm") apply false
  kotlin("plugin.spring") apply false

  id("org.springframework.boot") apply false
  id("io.spring.dependency-management")

  id("io.gitlab.arturbosch.detekt") apply false
  jacoco

  `maven-publish`
  signing

  id("com.github.ben-manes.versions") version "0.39.0"

  id("org.jetbrains.dokka") apply false
  id("org.asciidoctor.jvm.convert")
}

jacoco {
  toolVersion = "0.8.7"
}

allprojects {
  apply(plugin = "idea")
  apply(plugin = "io.spring.dependency-management")

  group = "io.github.qualersoft.robotframework"

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

      dependencySet(group = "io.kotest", version = "4.6.0") {
        entry("kotest-runner-junit5-jvm")
        entry("kotest-assertions-core-jvm")
        entry("kotest-property-jvm")
      }

      dependency(group = "org.robotframework", name = "robotframework", version = "4.1.2")

      dependency(group = "ch.qos.logback", name = "logback-classic", version = "1.2.6")

      dependencySet(group = "org.springframework.boot", version = "2.4.4") {
        entry("spring-boot-starter")
      }

      // add groovy to allow spring bean definition in groovy-style
      dependency(group = "org.codehaus.groovy", name = "groovy", version = "3.0.7")
    }
  }
}

val isReleaseVersion = version.toString().endsWith("snapshot", true)

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jetbrains.kotlin.plugin.spring")

  apply(plugin = "jacoco")
  apply(plugin = "io.gitlab.arturbosch.detekt")

  apply(plugin = "org.jetbrains.dokka")

  apply(plugin = "maven-publish")
  apply(plugin = "signing")

  jacoco {
    toolVersion = "0.8.7"
  }

  dependencies {
    implementation(group = "ch.qos.logback", name = "logback-classic")
  }

  configure<DetektExtension> {
    allRules = true
    config = files("$rootDir/detekt.yml")
    source = files("src/main/kotlin")
  }

  tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
      html.required.set(true)
      xml.required.set(true)
      txt.required.set(false)
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.withType<JacocoReport>())
  }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.toString()
      apiVersion = "1.5"
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
    dependsOn(dokkaJavadoc)
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
            val ghUsrnm: String? by project
            val ghToken: String? by project
            username = ghUsrnm
            password = ghToken
          }
        }
        maven {
          name = "central"
          val path = if(isReleaseVersion) { "content/repositories/snapshots" } else { "service/local/staging/deploy/maven2" }
          url = uri("https://s01.oss.sonatype.org/$path/")
          credentials {
            val mvnCntrlUsr: String? by project
            val mvnCntrlPswd: String? by project
            username = mvnCntrlUsr
            password = mvnCntrlPswd
          }
        }
      }
      register<MavenPublication>("maven") {
        from(components["java"])
        versionMapping {
          usage("java-api") {
            fromResolutionOf("runtimeClasspath")
          }
          usage("java-runtime") {
            fromResolutionResult()
          }
        }
        pom {
          url.set("https://github.com/qualersoft/robotframework-kolib")
          licenses {
            license {
              name.set("The Apache License, Version 2.0")
              url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
              distribution.set("repo")
            }
          }
          developers {
            developer {
              id.set("mathze")
              name.set("mathze")
              email.set("270275+mathze@users.noreply.github.com")
              url.set("https://github.com/mathze")
              organization.set("QualerSoft")
              organizationUrl.set("https://qualersoft.github.io/")
            }
          }
          scm {
            url.set("https://github.com/qualersoft/robotframework-kolib.git")
            connection.set("scm:git:git://github.com/qualersoft/robotframework-kolib.git")
            developerConnection.set("scm:git:git@github.com:qualersoft/robotframework-kolib.git")
            tag.set("HEAD")
          }
        }
      }
    }
  }

  signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
  }

  tasks.withType<Sign> {
    onlyIf { isReleaseVersion }
  }
}

tasks.register<JacocoReport>("jacocoRootReport") {
  group = "verification"
  subprojects.forEach {
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
