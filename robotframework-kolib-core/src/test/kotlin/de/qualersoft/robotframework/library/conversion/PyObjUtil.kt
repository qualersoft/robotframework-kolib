package de.qualersoft.robotframework.library.conversion

import org.python.core.PyObject
import org.python.util.PythonInterpreter

object PyObjUtil {

  fun create(script: String, retrieval: String = "res"): PyObject {
    val interpreter = PythonInterpreter()
    interpreter.exec(script)
    return interpreter.get(retrieval)
  }
}
