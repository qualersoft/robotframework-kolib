package de.qualersoft.robotframework.library

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationContextFactory
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigRegistry
import kotlin.reflect.KClass

class LibraryContext(vararg args: String, root: KClass<*>) {

  private val logger = LoggerFactory.getLogger(javaClass)
  val ctx: ConfigurableApplicationContext

  init {
    val app = SpringApplication(root.java)
    registerRootIfRequired(root, app)

    @SuppressWarnings("SpreadOperator")
    ctx = app.run(*args)
    ctx.registerShutdownHook()
    logger.debug("${this::class} initialized ${root.simpleName}")
  }

  private fun registerRootIfRequired(root: KClass<*>, app: SpringApplication) {
    if (root.constructors.isEmpty()) {
      app.setApplicationContextFactory { type ->
        ApplicationContextFactory.DEFAULT.create(type).also {
          if (it is AnnotationConfigRegistry) {
            // bypass spring requires the 'main' source class be constructable
            it.register(root.java)
          }
        }
      }
    }
  }
}

