package de.qualersoft.robotframework.library.binding

interface RfTagSupport {

  /**
   * Gets the tags of a keyword, if any.
   *
   * See also: [getting-keyword-tags](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#getting-keyword-tags)
   *
   * @param name The name of the keyword
   *
   * @return A list of tags or empty list (default).
   *
   * @since RF 3.0.2
   */
  fun getKeywordTags(name: String): List<String> = emptyList()
}
