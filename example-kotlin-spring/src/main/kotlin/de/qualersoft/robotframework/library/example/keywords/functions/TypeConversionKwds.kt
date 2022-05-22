package de.qualersoft.robotframework.library.example.keywords.functions

import de.qualersoft.robotframework.library.annotation.Keyword
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.LocalDateTime
import javax.annotation.ManagedBean

@ManagedBean
class TypeConversionKwds {

  //<editor-fold desc="Description">
  @Keyword
  fun tcCallWithString(arg: String?) = createResult(arg)

  @Keyword
  fun tcCallWithBoolean(arg: Boolean?) = createResult(arg)

  @Keyword
  fun tcCallWithByte(arg: Byte?) = createResult(arg)

  @Keyword
  fun tcCallWithShort(arg: Short?) = createResult(arg)

  @Keyword
  fun tcCallWithInt(arg: Int?) = createResult(arg)

  @Keyword
  fun tcCallWithLong(arg: Long?) = createResult(arg)

  @Keyword
  fun tcCallWithFloat(arg: Float?) = createResult(arg)

  @Keyword
  fun tcCallWithDouble(arg: Double?) = createResult(arg)
  //</editor-fold>

  //<editor-fold desc="Complex types">
  @Keyword
  fun tcCallWithBigInteger(arg: BigInteger?) = createResult(arg)

  @Keyword
  fun tcCallWithBigDecimal(arg: BigDecimal?) = createResult(arg)

  @Keyword
  fun tcCallWithDateTime(arg: LocalDateTime?) = createResult(arg)

  @Keyword
  fun tcCallWithDeltaTime(arg: Duration?) = createResult(arg)

  @Keyword
  fun tcCallWithByteArray(arg: ByteArray?) = createResult(arg) { arr ->
    "Got ${arr.map { it.toString() }.joinToString(",", "[", "]")}"
  }
  //</editor-fold>

  fun <T> createResult(arg: T?, otherWise: (T) -> String = { "Got $it" }): String = if (null == arg) {
    "Got <null>"
  } else {
    otherWise(arg)
  }
}
