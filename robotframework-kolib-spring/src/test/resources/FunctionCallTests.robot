*** Settings ***
Documentation    Test suite for dynamic keyword resulution
Library   de.qualersoft.robotframework.dummypack.DummyLib

*** Test Cases ***                                          # args, kwargs
Named-only with positional and varargs
  ${res}=   Dynamic    argument       named=xxx             # [argument], {named=xxx}
  Log To Console    ${res}
  ${res}=   Dynamic    a1             a2         named=3    # [a1, a2], {named=3}
  Log To Console    ${res}

Named-only with normal named
  ${res}=   Dynamic    named=foo      positional=bar        # [], {named=foo, positional=bar}
  Log To Console    ${res}

Named-only with free named
  ${res}=   Dynamic    named=value    foo=bar               # [], {named=value, foo=bar}
  Log To Console    ${res}
  ${res}=   Dynamic    named2=2       third=3    named=1    # [], {named=1, named2=2, third=3}
  Log To Console    ${res}

# full-fledged keyword                                      (pos1, pos2), [varargs], {kwargs}
Just mandatory positional
  ${res}=   Full keyword    pos1
  Log to console  ${res}                                  # (pos1, default), [], {}

Only both positional
  ${res}=   Full keyword    pos1  pos2
  Log to console  ${res}                                  # (pos1, pos2), [], {}

Positional and one vararg
  ${res}=   Full keyword    pos1  pos2  va1
  Log to console  ${res}                                  # (pos1, pos2), [va1], {}

Positional and two vararg
  ${res}=   Full keyword    pos1  pos2  va1  va2
  Log to console  ${res}                                  # (pos1, pos2), [va1, va2], {}

Positional and one kwarg
  ${res}=   Full keyword    pos1  pos2  kwk=1
  Log to console  ${res}                                  # (pos1, pos2), [], {kwk=1}

Override positional named argument fails
  ${res}=   Full keyword    pos1  pos2  kwk=1  pos1=override
  Log to console  ${res}                                  # error override

Merge kwargs
  &{map}=   Create dictionary  kwk1=1
  ${res}=   Full keyword   arg  pos2  kwargs=${map}  kwk2=2  kwk3=3
  Log to console  ${res}                                  # (arg, pos2), [], {kwk1=1, kwk2=2, kwk3=3}

# other tests

OneArgOnly
  ${res}=   Plain arg   test name
  Log To Console    ${res}

Get Config
  ${res}=   Get Config Dummy
  Log To Console    ${res}
