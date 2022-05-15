package de.qualersoft.robotframework.library.conversion

import kotlin.reflect.KClass

object EnumConverter {

  fun convertToEnum(targetType: KClass<*>, value: Any): Any {
    // 1. get enums
    val enums = targetType.java.enumConstants.map { it as Enum<*> }
    var candidates = enums.filter {
      it.name.equals(value.toString(), true) || // Plain match
        // ignore spaces and underscores
        it.name.replace(' ', '_').equals(value.toString().replace(' ', '_'), true)
    }

    if (candidates.isEmpty()) {
      throw ClassCastException(
        "Unable to cast value '$value' to enum '${targetType.simpleName}'! Valid values are: $enums."
      )
    }

    if (1 < candidates.size) {
      // we are going for exact case insensitive match
      val msg = """Unable to cast value '$value' to enum '${targetType.simpleName}' because multiple candidates found!
        |Please specify enum exact or consider renaming your enums. Valid values are: $enums.
      """.trimMargin()
      candidates = candidates.filter { it.name.equals(value.toString(), true) }
      if (candidates.isEmpty()) {
        // no match so still don't know which one to take
        throw ClassCastException(msg)
      }
      if (1 < candidates.size) {
        // we are going for exact match
        candidates = candidates.filter { it.name == value.toString() }
        if (candidates.isEmpty()) {
          // no match so still don't know which one to take
          throw ClassCastException(msg)
        }
      }
    }

    return candidates.first()
  }
}
