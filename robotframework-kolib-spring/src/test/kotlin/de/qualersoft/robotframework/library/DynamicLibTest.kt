package de.qualersoft.robotframework.library

import io.github.classgraph.ClassGraph
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.string.shouldContain
import org.springframework.util.ResourceUtils
import java.io.File
import java.util.concurrent.TimeUnit

class DynamicLibTest : AnnotationSpec(){

  @Test
  fun testPlainArg() {
    val result = runRobot("OneArgOnly")
    result shouldContain "Got name test name"
  }

  private fun runRobot(test: String): String {
    val suite = File(ResourceUtils.getURL("FunctionalCallTest.robot").file).absolutePath
    val classpathUrls = ClassGraph().classpathFiles
    val cp = "-cp " + classpathUrls.joinToString(separator = ";") { "\"${it.absolutePath}\"" }
    val args = arrayOf("run", "--test", test, suite).joinToString(" ")
    val proc = Runtime.getRuntime().exec("java $cp org.robotframework.RobotFramework $args")
    try {
      proc.waitFor(5, TimeUnit.MINUTES)
    } catch (ignored: InterruptedException) {
      println("Got Timeout exception :/")
      proc.destroyForcibly()
    }
    return proc.inputStream.readAllBytes().decodeToString()
  }
}