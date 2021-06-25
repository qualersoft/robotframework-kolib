package de.qualersoft.robotframework.library.binding

/**
 * Minimal interface that each
 * [dynamic robot framework library](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#dynamic-library-api)
 * has to implement.
 */
interface MinimalDynamicLibrary {

  /**
   * Implements the Robot Framework interface method 'get_keyword_names' required for dynamic libraries.
   *
   * @return List of [String]s containing all keyword names exposed by the library.
   */
  fun getKeywordNames(): List<String>

  /**
   * Robot Framework interface method 'run_keyword' required for dynamic libraries.
   *
   * __Remark__:
   * >If your library also implements [RfKwArgsSupport], robot framework will call
   * >[RfKwArgsSupport.runKeyword] instead of this!
   *
   * @param name The Name of keyword to be executed
   * @param args A list of positional arguments given to the keyword in the test data
   *
   * @return result of the keyword execution. `null` may also indicate `void` call
   */
  fun runKeyword(name: String, args: List<Any?>): Any?
}
