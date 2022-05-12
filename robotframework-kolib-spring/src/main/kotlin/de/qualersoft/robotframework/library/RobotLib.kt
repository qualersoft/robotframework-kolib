package de.qualersoft.robotframework.library

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.binding.MinimalDynamicLibrary
import de.qualersoft.robotframework.library.binding.RfArgumentSpecSupport
import de.qualersoft.robotframework.library.binding.RfArgumentTypesSupport
import de.qualersoft.robotframework.library.binding.RfKwArgsSupport
import de.qualersoft.robotframework.library.binding.RfLibdocSupport
import de.qualersoft.robotframework.library.binding.RfSourceSupport
import de.qualersoft.robotframework.library.model.KeywordDescriptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberFunctions

/**
 * Simply extend a class by this library and provide a entry-point class (root) from which to start scanning.
 *
 * **Remarks:**
 *
 * The robot framework documentation stats to create a Library-class at `empty` package level to avoid
 * long paths when using in robot-files.
 *
 * If you do so, you can simply create a marker interface/class in your sub package.
 *
 * **Attention:**
 *
 * Do not force springs class scanning to operate on the `empty` package! This may either
 * lead to out of memory exception or take a large initialization time.
 *
 * Try to put [root] as close as possible to your implementation.
 */
open class RobotLib(private val root: KClass<*>, vararg args: String) : MinimalDynamicLibrary, RfKwArgsSupport,
  RfArgumentSpecSupport, RfArgumentTypesSupport, RfLibdocSupport, RfSourceSupport {

  private val log: Logger = LoggerFactory.getLogger(javaClass)
  protected val ctx: ConfigurableApplicationContext

  private val kwdBeans: List<KClass<*>> by lazy { findKeywordBeans() }

  private val keyWords: Map<String, KeywordDescriptor> by lazy { findKeywordFunctions() }

  private val rootPath by lazy {
    val src = root.java.protectionDomain.codeSource
    return@lazy if (null != src) {
      File(src.location.toURI())
    } else {
      val path = root.java.getResource("${root.java.simpleName}.class")?.path?.let {
        it.substring(it.indexOf(':') + 1, it.indexOf('!'))
      }
      if (null == path) {
        null
      } else {
        val jarFilePath = URLDecoder.decode(path, StandardCharsets.UTF_8)
        File(jarFilePath)
      }
    }
  }

  init {
    log.debug("Initializing ${this::class.simpleName} with ${root.simpleName}")
    @SuppressWarnings("SpreadOperator")
    ctx = LibraryContext(*args, root = root).ctx
    log.debug("${this::class} initialized ${root.simpleName}")
  }

  /**
   * Constructor that easily accepts java-classes with out the need of the user to deal with reflection stuff.
   */
  @SuppressWarnings("SpreadOperator")
  constructor(root: Class<*>, vararg args: String) : this(Reflection.createKotlinClass(root), *args)

  final override fun getKeywordNames(): List<String> {
    return keyWords.keys.toList()
  }

  // not called because we also implement the kwdarg version
  final override fun runKeyword(name: String, args: List<Any?>): Any? = null

  final override fun runKeyword(name: String, args: List<Any?>, kwArgs: Map<String, Any?>): Any? {
    log.debug("Running keyword $name with args $args and kwArgs $kwArgs")
    val kwd = keyWords.getValue(name)

    val obj = ctx.getBean(kwd.declaringClass.java)

    return kwd.invoke(obj, args, kwArgs)
  }

  final override fun getKeywordArguments(name: String): List<List<Any?>> {
    val kwd = keyWords.getValue(name)
    val kwdArgs = kwd.robotArguments
    log.debug("Calculated arguments for keyword '$name' are $kwdArgs")
    return kwdArgs
  }

  final override fun getKeywordTypes(name: String): Map<String, Any> {
    val kwd = keyWords.getValue(name)
    val argTypes = kwd.robotArgumentTypes
    log.debug("Calculated argument types for keyword '$name' are $argTypes")
    return argTypes
  }

  /**
   * Remarks:
   *
   * `__intro__` and `__init__` are automatically redirect to [getLibraryGeneralDocumentation] and
   * [getLibraryUsageDocumentation] respectively.
   */
  final override fun getKeywordDocumentation(name: String): String = when (name) {
    "__intro__" -> getLibraryGeneralDocumentation()
    "__init__" -> getLibraryUsageDocumentation()
    else -> keyWords.getValue(name).description
  }

  final override fun getKeywordSource(name: String): String? {
    val kwd = keyWords.getValue(name)
    val pathToJar = rootPath ?: return null
    val classPath = kwd.declaringClass.qualifiedName?.replace('.', File.separatorChar)
    return "$pathToJar${File.separatorChar}$classPath"
  }

  /**
   * Return an instance, which may be shared or independent, of the specified bean.
   *
   * Allows for specifying explicit constructor arguments / factory method arguments,
   * overriding the specified default arguments (if any) in the bean definition.
   *
   * For details see [`BeanFactory.getBean(Class<T>, Object...)`] [org.springframework.beans.factory.BeanFactory.getBean].
   *
   * @param args arguments to use when creating a bean instance using explicit arguments
   *
   * @return an instance of the bean
   */
  protected inline fun <reified T> getBean(vararg args: Any?): T {
    return ctx.getBean(T::class.java, *args)
  }

  /**
   * Return an instance, which may be shared or independent, of the specified bean.
   *
   * Allows for specifying explicit constructor arguments / factory method arguments,
   * overriding the specified default arguments (if any) in the bean definition.
   *
   * For details see [`BeanFactory.getBean(String, Object...)`] [org.springframework.beans.factory.BeanFactory.getBean].
   *
   * @param name the name of the bean to retrieve
   * @param args arguments to use when creating a bean instance using explicit arguments
   * (only applied when creating a new instance as opposed to retrieving an existing one)
   *
   * @return an instance of the bean
   */
  protected fun getBean(name: String, vararg args: Any?): Any {
    return ctx.getBean(name, *args)
  }

  /**
   * Meant to be overwritten. Default impl returns empty string
   */
  @SuppressWarnings("FunctionOnlyReturningConstant")
  protected open fun getLibraryGeneralDocumentation(): String = ""

  /**
   * Meant to be overwritten. Default impl returns empty string
   */
  @SuppressWarnings("FunctionOnlyReturningConstant")
  protected open fun getLibraryUsageDocumentation(): String = ""

  private fun findKeywordBeans(): List<KClass<*>> = ctx.beanDefinitionNames.mapNotNull {
    val bd = ctx.beanFactory.getBeanDefinition(it)
    bd.beanClassName
  }.map { beanClassName ->
    Class.forName(beanClassName).kotlin
  }.filter { clz ->
    clz.memberFunctions.any { it.isAnnotationPresent(KwdClass.KCLASS) && it.visibility == KVisibility.PUBLIC }
  }

  private fun findKeywordFunctions(): Map<String, KeywordDescriptor> = kwdBeans.flatMap {
    it.memberFunctions.toList()
  }.filter {
    it.isAnnotationPresent(KwdClass.KCLASS)
  }.associate {
    val dto = KeywordDescriptor(it)
    dto.name to dto
  }

  private fun KFunction<*>.isAnnotationPresent(clazz: KClass<out Annotation>): Boolean =
    this.annotations.any { it.annotationClass == clazz }

  object KwdClass {
    val KCLASS = Keyword::class
  }
}
