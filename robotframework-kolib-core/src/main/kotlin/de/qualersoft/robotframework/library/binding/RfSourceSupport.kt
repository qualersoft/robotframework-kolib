package de.qualersoft.robotframework.library.binding

/**
 * The interface a library has to implement **iff** you want to have
 * source information available to robot framework.
 * Useful for tools using go-to.
 * Also useful if you implement [RfLibdocSupport], so the source will also be integrated.
 */
interface RfSourceSupport {
  /**
   * Gets the full-qualified path to the class that implements the keyword.
   * The returned path may also contain the line number. The line number has to
   * be appended to the path in the format `<path>[:<lineno>]`
   */
  fun getKeywordSource(name: String): String?
}
