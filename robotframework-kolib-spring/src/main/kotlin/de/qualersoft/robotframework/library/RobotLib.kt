package de.qualersoft.robotframework.library

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.binding.MinimalDynamicLibrary
import de.qualersoft.robotframework.library.binding.RFArgumentSpecSupport
import de.qualersoft.robotframework.library.binding.RFArgumentTypesSupport
import de.qualersoft.robotframework.library.binding.RFKwArgsSupport
import de.qualersoft.robotframework.library.binding.RfLibdocSupport
import de.qualersoft.robotframework.library.model.KeywordDescriptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberFunctions

open class RobotLib(root: KClass<*>, vararg args: String) : MinimalDynamicLibrary, RFKwArgsSupport,
  RFArgumentSpecSupport, RFArgumentTypesSupport, RfLibdocSupport {

  private val log: Logger = LoggerFactory.getLogger(javaClass)
  private val ctx: ConfigurableApplicationContext

  private val kwdBeans: List<KClass<*>> by lazy { findKeywordBeans() }

  private val keyWords: Map<String, KeywordDescriptor> by lazy { findKeywordFunctions() }


  init {
    log.debug("Initializing ${this::class.simpleName} with ${root.simpleName}")
    @SuppressWarnings("SpreadOperator")
    ctx = LibraryContext(*args, root = root).ctx
    log.debug("${this::class} initialized ${root.simpleName}")
  }

  @SuppressWarnings("SpreadOperator")
  constructor(root: Class<*>, vararg args: String): this(Reflection.createKotlinClass(root), *args)

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

  /**
   * Meant to be overwritten. Default impl returns empty string
   */
  @SuppressWarnings("FunctionOnlyReturningConstant")
  protected fun getLibraryGeneralDocumentation(): String = ""

  /**
   * Meant to be overwritten. Default impl returns empty string
   */
  @SuppressWarnings("FunctionOnlyReturningConstant")
  protected fun getLibraryUsageDocumentation(): String = ""

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