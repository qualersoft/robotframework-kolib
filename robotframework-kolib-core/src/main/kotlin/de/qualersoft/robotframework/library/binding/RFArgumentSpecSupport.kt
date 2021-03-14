package de.qualersoft.robotframework.library.binding

/**
 * Interface for [https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#getting-keyword-arguments]
 */
interface RFArgumentSpecSupport {

  /**
   * Retrieves parameter name (and default value) for the given keyword.
   *
   * @param name Name of the keyword
   * @return List of `pair` of parameters name and default value
   */
  fun getKeywordArguments(name: String): List<List<Any?>>
}