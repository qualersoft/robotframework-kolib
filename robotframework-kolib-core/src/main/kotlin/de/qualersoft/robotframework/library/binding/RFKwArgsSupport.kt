package de.qualersoft.robotframework.library.binding

interface RFKwArgsSupport {

  /**
   * Robot Framework interface method 'run_keyword' required for dynamic libraries.
   *
   * @param name The Name of keyword to be executed
   * @param args A list of positional arguments given to the keyword in the test data
   * @param kwArgs An optional `Map` containing named arguments (since RF 3.2)
   *
   * @return result of the keyword execution. `null` may also indicate `void` call
   */
  fun runKeyword(name: String, args: List<Any?>, kwArgs: Map<String, Any?>): Any?
}