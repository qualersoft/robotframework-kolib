package de.qualersoft.robotframework.library

import de.qualersoft.robotframework.dummypack.DummyMarker
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

class RobotLibPackageTest  : FreeSpec({
  "Found simple keyword" {
    val actual = getPckgKwdNames()
    actual shouldContain "getSimple"
  }
  "Found complex keyword" {
    val actual = getPckgKwdNames()
    actual shouldContain "getConfigDummy"
  }
  "Calling with configuration should return value from yml" {
    val actual = execPckgKwd("getConfigDummy") as String
    actual shouldBe "Hello from im a dummy"
  }
})

fun getPckgKwdNames() = RobotLib(root = DummyMarker::class).getKeywordNames()
fun execPckgKwd(name: String) = RobotLib(root=DummyMarker::class).runKeyword(name, emptyList(), emptyMap())