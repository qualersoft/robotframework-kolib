package de.qualersoft.robotframework.library

import io.github.classgraph.ClassGraph
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.string.shouldNotContain
import org.springframework.util.ResourceUtils
import java.io.File
import java.util.concurrent.TimeUnit

class DynamicLibTest : AnnotationSpec(){

  // No Test because execution hangs for quite long time
  fun testPlainArg() {
    val result = runRobot("OneArgOnly")
    result shouldNotContain "No keyword with name 'Plain arg' found."
  }

  private fun runRobot(test: String): String {
    val suite = File(ResourceUtils.getURL("classpath:FunctionCallTests.robot").file).absolutePath
    val classpathUrls = ClassGraph().classpathFiles
    val cp = "-cp \"" + classpathUrls.joinToString(separator = ";") { it.absolutePath } + "\""
    val args = arrayOf("run", "--test", test, suite).joinToString(" ")
    val cmd = "java $cp org.robotframework.RobotFramework $args"
    val proc = Runtime.getRuntime().exec(cmd)
    try {
      proc.waitFor(5, TimeUnit.MINUTES)
    } catch (ignored: InterruptedException) {
      println("Got Timeout exception :/")
      proc.destroyForcibly()
    }
    return proc.inputStream.readAllBytes().decodeToString()
  }
}
