package de.qualersoft.robotframework.library.binding

interface RfLibdocSupport {
  /**
   * Provides information about the library and the keywords
   *
   * @param name Name of the keyword or one of `__intro__` or `__init__`
   *
   * @return The documentation
   */
  fun getKeywordDocumentation(name: String): String
}