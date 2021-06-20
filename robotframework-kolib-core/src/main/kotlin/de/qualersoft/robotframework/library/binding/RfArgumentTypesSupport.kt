package de.qualersoft.robotframework.library.binding

/**
 * Interface to provide Robot Framework with
 * [type information](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#getting-keyword-argument-types)
 * of a keyword's arguments.
 *
 * This enables Robot Framework for
 * [automatic argument conversion](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#argument-types).
 *
 * @since RF 3.1
 */
interface RfArgumentTypesSupport {

  /**
   * Retrieves the type of a keyword's arguments.
   *
   * See also [getting-keyword-argument-types](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#getting-keyword-argument-types)
   *
   * @param name the keyword's name
   *
   * @return a "parameter-name `to` type" map.
   *
   * @since RF 3.1
   */
  fun getKeywordTypes(name: String): Map<String, Any>
}
