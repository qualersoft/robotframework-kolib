package de.qualersoft.robotframework.library

import de.qualersoft.robotframework.library.annotation.Keyword
import de.qualersoft.robotframework.library.binding.*
import de.qualersoft.robotframework.library.model.KeywordArgDto
import de.qualersoft.robotframework.library.model.KeywordDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

open class RobotLib(vararg args: String, root: KClass<*>) : MinimalDynamicLibrary, RFKwArgsSupport,
  RFArgumentSpecSupport, RFArgumentTypesSupport, RfLibdocSupport {

  private val log: Logger = LoggerFactory.getLogger(javaClass)
  private val ctx: ConfigurableApplicationContext

  // backing property
  private var _kwdBeans: List<KClass<*>>? = null
  private val kwdBeans: List<KClass<*>>
    get() {
      if (null == _kwdBeans) {
        _kwdBeans = findKeywordBeans()
      }
      return _kwdBeans!!
    }

  // backing property
  private var _kwdList: Map<String, KeywordDto>? = null
  private val keyWords: Map<String, KeywordDto>
    get() {
      if (null == _kwdList) {
        _kwdList = findKeywordFunctions()
      }
      return _kwdList!!
    }


  init {
    log.debug("Initializing ${this::class.simpleName} with ${root.simpleName}")
    ctx = LibraryContext(*args, root = root).ctx
    log.debug("${this::class} initialized ${root.simpleName}")
  }

  final override fun getKeywordNames(): List<String> {
    return keyWords.keys.toList()
  }

  // not called because we also implement the kwdarg version
  final override fun runKeyword(name: String, args: List<Any?>): Any? = null

  final override fun runKeyword(name: String, args: List<Any?>, kwArgs: Map<String, Any?>): Any? {
    log.debug("Running keyword $name with args $args and kwArgs $kwArgs")
    val kwd = keyWords.getValue(name)

    val obj = ctx.getBean(kwd.declaringClass)
    val cArgs = mutableListOf<Any?>(obj)
    cArgs.addAll(prepareArguments(kwd, args, kwArgs))
    return kwd.method.call(*cArgs.toTypedArray())
  }

  final override fun getKeywordArguments(name: String): List<List<Any?>> {
    val kwd = keyWords.getValue(name)
    val kwdArgs = kwd.arguments.map { it.toArgumentTuple() }
    log.debug("Calculated arguments for keyword '$name' are $kwdArgs")
    return kwdArgs
  }

  final override fun getKeywordTypes(name: String): Map<String, KClass<*>> {
    val kwd = keyWords.getValue(name)
    val argTypes = kwd.arguments.map { it.name to it.type }.toMap()
    log.debug("Calculated argument types for keyword '$name' are $argTypes")
    return argTypes
  }

  final override fun getKeywordDocumentation(name: String): String = when (name) {
    "__intro__" -> getLibraryGeneralDocumentation()
    "__init__" -> getLibraryUsageDocumentation()
    else -> keyWords.getValue(name).description
  }

  protected fun getLibraryGeneralDocumentation(): String = ""

  protected fun getLibraryUsageDocumentation(): String = ""

  private fun findKeywordBeans(): List<KClass<*>> = ctx.beanDefinitionNames.mapNotNull {
    val bd = ctx.beanFactory.getBeanDefinition(it)
    bd.beanClassName
  }.map { beanClassName ->
    Class.forName(beanClassName).kotlin
  }.filter { clz ->
    clz.memberFunctions.any { it.isAnnotationPresent(KwdClass.KCLASS) && it.visibility == KVisibility.PUBLIC }
  }

  private fun findKeywordFunctions(): Map<String, KeywordDto> = kwdBeans.flatMap {
    it.memberFunctions.toList()
  }.filter {
    it.isAnnotationPresent(KwdClass.KCLASS)
  }.map {
    val dto = KeywordDto(it)
    dto.name to dto
  }.toMap()

  private fun KFunction<*>.isAnnotationPresent(clazz: KClass<out Annotation>): Boolean =
    this.annotations.any { it.annotationClass == clazz }

  private fun prepareArguments(kwd: KeywordDto, args: List<Any?>, kwArgs: Map<String, Any?>): List<Any?> {
    val definedArgs = kwd.arguments
    val res = Array<Any?>(definedArgs.size) { null }
    definedArgs.forEachIndexed { idx, kwdArg ->
      when {
        idx < args.size -> {
          res[idx] = args[idx]
        }
        kwdArg.kind == KeywordArgDto.ParamKind.KWARG -> {
          res[idx] = kwArgs
        }
        else -> { // everything here must be an optional argument
          val default = if (kwdArg.nullable && null == kwdArg.argAnnotation) null else kwdArg.argAnnotation!!.default
          res[idx] = if (kwdArg.nullable &&
            (("\u0000" == default) || (null == default))
          ) {
            null
          } else convertDefaultToType(kwdArg.type, default!!)
        }
      }
    }
    return res.toList()
  }

  private fun convertDefaultToType(type: KClass<*>, value: String): Any {
    return when (type) {
      Boolean::class -> value.toBoolean()
      Byte::class -> value.toByte()
      ByteArray::class -> value.toByte()
      Int::class -> value.toInt()
      Float::class -> value.toFloat()
      Double::class -> value.toDouble()
      String::class -> value
      else -> type.constructors.single { it.isAccessible && it.parameters.size == 1 && it.parameters[0].type.classifier == String::class }
        .call(value)
    }
  }

  object KwdClass {
    val KCLASS = Keyword::class
  }
}