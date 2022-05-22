package de.qualersoft.robotframework.library

private const val NL = "\n"

/**
 * Applies similar transformation as java text block does.
 * With this, it is possible to spread a single line text in output over multiple lines in code.
 *
 * **Remark**:
 * This implementation does not support `\s` as it is illegal escape sequence for kotlin.
 * But as we don't remove trailing whitespaces, this feature isn't needed.
 * 
 * *Example*
 * ```
 * print("""
 *     This is a \
 *     single line \
 *     in output \
 *     but raw string \
 *     in code.
 *     
 *     And this should also
 *     work.""".trimAsTextBlock())
 * ```
 * Will give the following output
 * ```
 * This is a single line in output but raw string in code.
 * 
 * And this should also
 * work.
 * ```
 */
fun String.trimAsTextBlock() = this.trimIndent()
  .replace("""\$NL""", "")

inline fun String.ifNotBlank(transform:(String) -> String) = if(this.isNotBlank()) transform(this) else this
