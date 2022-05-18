*** Settings ***
Library       JExampleLib

*** Test Cases ***
Call vararg fnc with position only
  Fn Call With Varargs  just pos

Call vararg fnc with position and varargs
  Fn Call With Varargs  position    firstVararg   secondVararg    third Vararg

Call kwarg fnc with position only
  Fn Call with kwargs   just pos

Call kwarg fnc with position and kwargs
  Fn Call with kwargs   just pos  key1=val 1  key2=snd Val

Call var- and kw-arg fnc with just position
  Fn Call With Var And KwArgs  just pos

Call var- and kw-arg fnc with position and varargs
  Fn Call With Var And KwArgs  just pos   vArg1   vArg2

Call var- and kw-arg fnc with position and kwargs
  Fn Call With Var And KwArgs  just pos   key1=kwArg1   key2=kwArg2

Call var- and kw-arg fnc with position, varargs and kwargs
  Fn Call With Var And KwArgs  just pos   vArg1   vArg2   key1=kwArg1   key2=kwArg2
