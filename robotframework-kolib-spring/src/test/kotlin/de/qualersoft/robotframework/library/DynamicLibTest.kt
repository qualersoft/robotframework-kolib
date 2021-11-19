package de.qualersoft.robotframework.library

import io.github.classgraph.ClassGraph
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.assertAll
import org.springframework.util.ResourceUtils
import java.io.File

class DynamicLibTest : AnnotationSpec() {

  @Test
  fun testNamedOnlyWithPositionalAndVarargs() {
    val result = runRobot("Named-only with positional and varargs")
    assertAll(
      { result shouldContain "[argument], {named=xxx}" },
      { result shouldContain "[a1, a2], {named=3}" }
    )
  }

  @Test
  fun testNamedOnlyWithNormalNamed() {
    val result = runRobot("Named-only with normal named")
    result shouldContain "[], {named=foo, positional=bar}"
  }

  @Test
  fun testNamedOnlyWithFreeNamed() {
    val result = runRobot("Named-only with free named")
    assertAll(
      { result shouldContain "[], {named=value, foo=bar}" },
      { result shouldContain "[], {named=1, named2=2, third=3}" }
    )
  }

  @Test
  fun testMandatoryPositional() {
    val result = runRobot("Just mandatory positional")
    result shouldContain "(pos1, default), [], {}"
  }

  @Test
  fun testPositionalOnly() {
    val result = runRobot("Only both positional")
    result shouldContain "(pos1, pos2), [], {}"
  }

  @Test
  fun testPositionalAndOneVararg() {
    val result = runRobot("Positional and one vararg")
    result shouldContain "(pos1, pos2), [va1], {}"
  }

  @Test
  fun testPositionalAndTwoVarargs() {
    val result = runRobot("Positional and two vararg")
    result shouldContain "(pos1, pos2), [va1, va2], {}"
  }

  @Test
  fun testPositionalAndOneKwarg() {
    val result = runRobot("Positional and one kwarg")
    result shouldContain "(pos1, pos2), [], {kwk=1}"
  }

  @Test
  fun testOverridePositionalByKwargFails() {
    val result = runRobot("Override positional named argument fails")
    result shouldContain "Full Keyword' got multiple values for argument 'pos1'"
  }

  @Test
  fun testMergeKwargs() {
    val result = runRobot("Merge kwargs")
    result shouldContain "(arg, pos2), [], {kwk1=1, kwk2=2, kwk3=3}"
  }

  @Test
  fun testPlainArg() {
    val result = runRobot("OneArgOnly")
    assertAll(
      { result shouldNotContain "No keyword with name 'Plain arg' found." },
      { result shouldContain "Got name test name" }
    )
  }

  @Test
  fun testGetConfig() {
    val result = runRobot("Get Config")
    result shouldContain "Hello from im a dummy"
  }

  private fun runRobot(test: String): String {
    val suite = '"' + File(ResourceUtils.getURL("classpath:FunctionCallTests.robot").file).absolutePath + '"'
    val classpathUrls = ClassGraph().classpathFiles.filter { !it.path.contains("\\wrapper\\dists\\gradle-") }
    val classes = '"' + classpathUrls.joinToString(separator = ";") { it.absolutePath } + '"'
    val cmdArr =
      arrayOf("java", "-cp", classes, "org.robotframework.RobotFramework", "run", "--test", '"' + test + '"', suite)
    val proc = Runtime.getRuntime().exec(cmdArr)
    proc.outputStream.close()
    proc.errorStream.close()
    return proc.inputStream.readAllBytes().decodeToString()
  }
}
