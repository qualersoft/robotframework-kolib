package de.qualersoft.robotframework.library.annotation

enum class LibraryScope {
  /**
   * Only one instance is created during the whole test execution and it is shared by all test cases and test suites.
   * Libraries created from modules are always global.
   */
  GLOBAL,

  /**
   * A new instance is created for every test suite.
   * The lowest-level test suites, created from test case files and containing test cases,
   * have instances of their own, and higher-level suites all get their own instances for their possible setups
   * and teardowns.
   */
  SUITE,

  @Deprecated("As of RF 3.2", ReplaceWith("SUITE"))
  TEST_SUITE,

  /**
   * A new instance is created for every test case. A possible suite setup and suite teardown share yet another instance.
   */
  TEST,

  @Deprecated("As of RF 3.2", ReplaceWith("TEST"))
  TEST_CASE,

  /**
   * Alias for [TEST] in case of RPA
   */
  TASK
}