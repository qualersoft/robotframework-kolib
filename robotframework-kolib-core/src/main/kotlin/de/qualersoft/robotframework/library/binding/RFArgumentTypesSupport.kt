package de.qualersoft.robotframework.library.binding

import kotlin.reflect.KClass

interface RFArgumentTypesSupport {
  /**
   * Retrieves the type of a keywords parameter.
   *
   * See also [getting-keyword-argument-types](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#getting-keyword-argument-types)
   *
   * @param name the name of the keyword
   *
   * @return a parameter-name `<>` type map
   *
   * @since RF 3.1
   */
  fun getKeywordTypes(name: String): Map<String, KClass<*>>
}