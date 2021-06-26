package de.qualersoft.robotframework.library.conversion

object BooleanConverter {

  fun convertToBoolean(value: Any): Any = when (value) {
    is String -> booleanOfString(value)
    is Number -> booleanOfNumber(value)
    else -> throw IllegalArgumentException("Unexpected type for value <$value>! Only Numbers and some Strings are allowed.")
  }

  private val TRUES = listOf("yes", "true", "on", "ok")
  private val FALSES = listOf("", "no", "false", "off", "nok")

  private fun booleanOfString(value: String): Boolean {
    val normalized = value.trim()
    return when {
      null != normalized.toDoubleOrNull() -> {
        booleanOfNumber(normalized.toDouble())
      }
      TRUES.any { normalized.equals(it, true) } -> {
        true
      }
      FALSES.any { normalized.equals(it, true) } -> {
        false
      }
      else -> {
        val strTrues = TRUES.joinToString("', '", "['", "']")
        val strFalses = FALSES.joinToString("', '", "['", "']")
        val msg = "Valid values for `true` are $strTrues, and for `false` are $strFalses"
        throw IllegalArgumentException("Unable to convert String <$value> to Boolean! $msg.")
      }
    }
  }

  /*
   * Simply convert to double and check if value is not equal to zero.
   */
  private fun booleanOfNumber(value: Number): Boolean = value.toDouble() != 0.0
}