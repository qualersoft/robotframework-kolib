package de.qualersoft.robotframework.library.model

import de.qualersoft.robotframework.library.annotation.Keyword
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.temporal.Temporal
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.cast
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.safeCast

open class KeywordDescriptor(private val function: KFunction<*>) {

  // 1. we get the keyword annotation
  private val annotation = function.annotations.first { it is Keyword } as Keyword

  // 2. we get the name;
  val name: String = annotation.name.ifBlank {
    function.name
  }

  // 3. get declaring class of the function
  val declaringClass: KClass<*> = detectDeclaringClass()

  private val parameters: List<KeywordParameterDescriptor>

  val description: String by lazy {
    """${normalizeSummary(annotation.docSummary)}
    ${createDetailsDescription(annotation.docDetails)}
    ${createParameterDoc()}""".trim()
  }

  init {
    validateFunction()

    // 4. parse parameter
    parameters = function.valueParameters.map { KeywordParameterDescriptor(it) }
  }

  val robotArgumentTypes by lazy {
    parameters.associate { it.name to toRobotType(it.type) }
  }

  val robotArguments by lazy {
    parameters.map { it.robotArgumentDescriptor }
  }

  /**
   * Maps a kotlin class to a robot framework type iff possible
   * otherwise the kotlin class itself will be returned
   */
  private fun toRobotType(originType: KClass<*>): Any {
    val result = when {
      originType.isSubclassOf(Number::class) -> {
        determineNumberType(originType as KClass<Number>)
      }
      originType == Boolean::class -> {
        "bool"
      }
      originType == String::class -> {
        String::class
      }
      originType == Date::class || originType.isSubclassOf(Temporal::class) -> {
        "datetime"
      }
      originType == Duration::class -> {
        "timedelta"
      }
      originType == ByteArray::class -> {
        "bytearray"
      }
      originType.isSubclassOf(Enum::class) -> {
        originType.superclasses.first { it.isSubclassOf(Enum::class) }
      }
      else -> originType
    }
    return result
  }

  private fun <T : Number> determineNumberType(type: KClass<T>): Any {
    return when (type) {
      Byte::class, Short::class, Int::class, Long::class -> "int"
      Float::class, Double::class -> "float"
      else -> type
    }
  }

  /**
   * Calls the function described by this object.
   * Therefore the given arguments are mapped from Robot Framework presentation to kotlin.
   *
   * @see mapArgumentsToParameterNames
   * @see mapAndValidatePreparedParameter
   */
  fun invoke(thisRef: Any, args: List<Any?>, kwArgs: Map<String, Any?>?): Any? {
    val namedValues = mutableMapOf<String, Any?>()
    if (!kwArgs.isNullOrEmpty()) {
      namedValues.putAll(kwArgs)
    }

    val callArgs = mutableMapOf<KParameter, Any?>(function.instanceParameter!! to thisRef)

    val params = parameters.toMutableList()
    params.sortBy { it.position }

    // 1. map rf args to argument-names
    val preparedArgs = mapArgumentsToParameterNames(params, args, namedValues)
    // 2. validate prepared arguments arg
    callArgs.putAll(mapAndValidatePreparedParameter(preparedArgs))

    return function.callBy(callArgs)
  }

  /**
   *
   * @see consumeKwArgs
   */
  private fun mapArgumentsToParameterNames(
    orderedParams: List<KeywordParameterDescriptor>,
    args: List<Any?>,
    kwArgs: Map<String, Any?>
  ): Map<String, Optional<Any>> {

    val result: MutableMap<String, Optional<Any>?> = orderedParams.associate { it.name to null }.toMutableMap()
    val validationErrors = mutableListOf<String>()

    // 1. we fill up with positional args
    var i = 0
    while (i < args.size) {
      val desc = orderedParams[i]
      if (ParameterKind.VARARG == desc.kind) {
        // we have to pack everything in single object
        // and as varargs has to be last 'arg' (kwArgs are separate)
        // we can consume everything
        val varargs = (i until args.size).map { args[it] }
        result[desc.name] = Optional.of(varargs.toList())
        i = args.size
      } else {
        // normal value argument
        result[desc.name] = Optional.ofNullable(args[i])
        ++i
      }
    }

    // 2. we fill up kwArgs with named args
    val argsMap = orderedParams.associateBy { it.name }
    val namedArgs = kwArgs.toMutableMap()
    for (k in kwArgs.keys) {
      val desc = argsMap[k]
      if (null != desc) {
        if (null != result[desc.name]) {
          validationErrors += "The parameter with name '$k' was already set from positional arguments"
        } else {
          val value = namedArgs.remove(k)
          result[desc.name] = Optional.ofNullable(value)
        }
      }
    }

    // 3. if kwArgs (namedArgs) left find the appropriate parameter
    validationErrors.addAll(consumeKwArgs(namedArgs, result))

    if (validationErrors.isNotEmpty()) {
      throw IllegalArgumentException(
        "When calling the keyword, following argument mapping errors occurred:\n\t" + validationErrors.joinToString(
          "\n\t"
        )
      )
    }

    return result.filterValues { null != it }.mapValues { it.value as Optional<Any> }
  }

  /**
   * Put the remaining `kwArgs` to an appropriate parameter marked with kwArg within the [result]-map.
   * If the target kwArg-Parameter already exists, remainders will be added otherwise a new entry
   * with the parameter name will be added to [result]
   *
   * @param remainingKwArgs The remaining kwArgs if any
   * @param result The so far processed target arguments of the function to call (k:= name of parameter; v:=argument from RF)
   *
   * @return A list of error messages.
   *
   * @see mapArgumentsToParameterNames
   */
  private fun consumeKwArgs(
    remainingKwArgs: MutableMap<String, Any?>,
    result: MutableMap<String, Optional<Any>?>
  ): List<String> {
    val validationErrors = mutableListOf<String>()
    if (remainingKwArgs.isNotEmpty()) {
      val desc = parameters.find { ParameterKind.KWARG == it.kind }
      if (null != desc) {
        val kwEntry = result[desc.name]
        if (null == kwEntry || kwEntry.isEmpty) { // not set so far
          result[desc.name] = Optional.of(remainingKwArgs)
        } else { // merge with existing
          try {
            // we already ensured type is Map<String, Any?> but better save than sorrow
            val originMap = kwEntry.get() as Map<*, *>
            val newMap = originMap.entries.associate { it.key.toString() to it.value }.toMutableMap()
            newMap.putAll(remainingKwArgs)
            result[desc.name] = Optional.of(newMap)
          } catch (ex: Exception) {
            validationErrors += "Unable to extend existing kwArg-map: ${kwEntry.get()}" +
                                " with remaining kwArgs $remainingKwArgs! $ex"
          }
        }
      } else {
        validationErrors += "No kwArg-Parameter found but arguments left! The following kwArgs are left over: $remainingKwArgs"
      }
    }
    return validationErrors
  }

  /**
   * value.isEmpty is interpreted as `null`
   */
  private fun mapAndValidatePreparedParameter(args: Map<String, Optional<Any>>): Map<KParameter, Any?> {
    val result = mutableMapOf<KParameter, Any?>()
    val validationErrors = mutableListOf<String>()
    for ((k, v) in args) {
      val desc = parameters.find { k == it.name }
      if (null == desc) {
        // This should not happen
        validationErrors += "Could not find a ParameterDescription with name $k"
      } else {
        // ATM simply pass to map
        // TODO Vallidate & convert
        result[desc.param] =
          convertToTargetType(desc, v)
      }
    }
    return result
  }

  private fun convertToTargetType(descriptor: KeywordParameterDescriptor, rawVal: Optional<Any>): Any? {
    return when {
      rawVal.isEmpty -> {
        val cls = descriptor.type
        cls.safeCast(null)
      }
      else -> {
        convertToType(descriptor.type, rawVal.get())
      }
    }
  }

  private fun convertToType(type: KClass<*>, value: Any): Any {
    // if type already match or is compatible return
    return if (type == value::class || type.isSuperclassOf(value::class)) {
      value
    } else {
      // else convert
      // todo: dynamically find `converters`
      when (type) {
        Boolean::class -> type.cast(value)
        isClassOf(type, Number::class) -> toNumberType(type, value)
        ByteArray::class -> (value as Collection<*>).map { toNumberType(Byte::class, it!!) as Byte }.toByteArray()
        isClassOf(type, Collection::class) -> (value as Collection<*>)
        isClassOf(type, Temporal::class) -> toTemporal(type, value)
        String::class -> value.toString()
        else -> type.constructors.single {
          it.isAccessible &&
          it.visibility == KVisibility.PUBLIC &&
          it.parameters.size == 1 &&
          (it.parameters[0].type.classifier as KClass<*>).isSuperclassOf(value::class)
        }.call(value)
      }
    }
  }

  private fun isClassOf(derived: KClass<*>, base: KClass<*>): KClass<*> = if (base.isSuperclassOf(derived)) {
    derived
  } else {
    Nothing::class
  }

  private fun toNumberType(type: KClass<*>, value: Any): Number {
    val candidates = value.javaClass.kotlin.members
      .filter { KVisibility.PUBLIC == it.visibility } // only public functions
      .filter { it.returnType.classifier as KClass<*> == type } // return type must match
      .filter { it.valueParameters.isEmpty() } // no arguments (except `this`)

    for(converter in candidates) {
      try {
        val param = converter.instanceParameter ?: converter.extensionReceiverParameter
        if (null != param) {
          val result = converter.callBy(mapOf(param to value as Number))
          return result as Number
        }
      } catch (ignore: Throwable) {
        LoggerFactory.getLogger(javaClass).debug("Caught conversion exception for value '$value' to '$type'", ignore)
        // noop
      }
    }

    throw UnsupportedOperationException("Couldn't find a matching number converter for `$value` to '$type'")
  }

  private fun toTemporal(type: KClass<*>, value: Any): Temporal {
    return if (value is String) {
      OffsetDateTime.parse(value)
    } else {
      type.cast(value as Temporal)
    } as Temporal
  }

  private fun findConstructor(targetType: KClass<*>, value: Any): Any {
    // get all public ctors with one argument
    val ctors = targetType.constructors.filter {
      KVisibility.PUBLIC == it.visibility &&
      it.valueParameters.size == 1 &&
      // atm we ignore the intersection types
      it.valueParameters[0].type.classifier is KClass<*>
    }
    // find the one with a compatible type
    var ctor = ctors.find { (it.parameters[0].type.classifier as KClass<*>).isSuperclassOf(targetType) }
    if (null != ctor) {
      return ctor.call(value)
    } else {
      // do we have a string ctor?
      ctor = ctors.find { (it.valueParameters[0].type.classifier as KClass<*>) == String::class }
      if (null != ctor) {
        return ctor.call(value.toString())
      }
    }
    throw UnsupportedOperationException("Could not find a converter for $value")
  }

  private fun getStringConstructor(type: KClass<*>) = type.constructors.single {
    it.isAccessible &&
    KVisibility.PUBLIC == it.visibility &&
    it.parameters.size == 1 &&
    it.parameters[0].type.classifier == String::class
  }

  /**
   * Accessible and not abstract
   */
  private fun validateFunction() {
    if (function.isAbstract) {
      throw IllegalArgumentException("Keyword functions may not abstract! Make '$name' concrete.")
    }
    if (KVisibility.PUBLIC != function.visibility) {
      throw IllegalArgumentException("Function '$name' is not accessible! Make it public.")
    }
  }

  /**
   * Retrieves the declaring class of this function.
   *
   * @throws IllegalArgumentException when given function is not within a real class
   */
  private fun detectDeclaringClass(): KClass<*> {
    val thisArg = function.instanceParameter
                  ?: throw IllegalArgumentException(
                    "Keyword annotation may only be placed on member functions! Move function '$name' to a class."
                  )
    try {
      return thisArg.type.classifier as KClass<*>
    } catch (npe: NullPointerException) {
      throw IllegalArgumentException(
        "Cannot retrieve class for method '$name'! Intersection types are not supported!",
        npe
      )
    } catch (cce: ClassCastException) {
      throw IllegalArgumentException("Something went totally wrong! Could not cast to KClass.", cce)
    }
  }

  private fun normalizeSummary(summary: Array<String>): String {
    return summary.map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ").let {
      if (it.isNotBlank()) {
        "*Summary*:\n\n$it\n"
      } else {
        ""
      }
    }
  }

  private fun createDetailsDescription(details: Array<String>): String {
    return if (details.isNotEmpty()) {
      details.joinToString("\n", prefix = "\n*Details*:\n\n") { it.trim() }
    } else {
      ""
    }
  }

  private fun createParameterDoc() =
    if (parameters.isNotEmpty()) {
      val sep = System.lineSeparator()
      "$sep*Parameters*:$sep" + parameters.joinToString(
        "$sep- ", prefix = "- "
      ) { it.documentation }
    } else {
      ""
    }
}