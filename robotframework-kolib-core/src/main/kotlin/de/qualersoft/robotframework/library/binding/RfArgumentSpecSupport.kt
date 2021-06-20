package de.qualersoft.robotframework.library.binding

/**
 * Interface for
 * [getting-keyword-arguments](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#getting-keyword-arguments)
 *
 * If a library only implements [MinimalDynamicLibrary] Robot Framework does not
 * have any information about the arguments that the implemented keywords accept.
 * This is problematic, because most real keywords expect a certain number of
 * keywords, and under these circumstances they would need to check the argument
 * counts themselves.
 */
interface RfArgumentSpecSupport {

  /**
   * Retrieves parameter name (and default value) for the given keyword.
   *
   * @param name Name of the keyword
   * @return List of `pair` of parameters name and default value
   */
  fun getKeywordArguments(name: String): List<List<Any?>>
}