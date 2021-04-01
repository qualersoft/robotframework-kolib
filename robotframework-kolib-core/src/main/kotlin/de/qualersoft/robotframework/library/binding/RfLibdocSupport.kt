package de.qualersoft.robotframework.library.binding

/**
 * The interface that a library has to implement **iff** you want to generate
 * library documentation through the
 * [`robot.libdoc`](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#libdoc)
 * command.
 *
 * You don't have to implement this interface, but this library provide you with good tools to easily
 * accomplish this task. And good documentation should not only apply to tests😉
 *
 * For further information see
 */
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