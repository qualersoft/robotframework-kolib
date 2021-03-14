package de.qualersoft.robotframework.library.binding

interface MinimalDynamicLibrary {

  /**
   * Implements the Robot Framework interface method 'get_keyword_names' required for dynamic libraries.
   *
   * @return List of [String]s containing all keyword names defined in the library.
   */
  fun getKeywordNames(): List<String>

  /**
   * Robot Framework interface method 'run_keyword' required for dynamic libraries.
   *
   * @param name The Name of keyword to be executed
   * @param args A list of positional arguments given to the keyword in the test data
   *
   * @return result of the keyword execution. `null` may also indicate `void` call
   */
  fun runKeyword(name: String, args: List<Any?>): Any?
}