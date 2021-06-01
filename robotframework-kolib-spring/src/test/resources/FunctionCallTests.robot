*** Settings ***
Documentation    Test suite for dynamic keyword resulution
Library   de.qualersoft.robotframework.dummypack.DummyLib

*** Test Cases ***                                  # args, kwargs
OneArgOnly
    Plain arg   test name

Named-only with positional and varargs
    Dynamic    argument       named=xxx             # [argument], {named: xxx}
    Dynamic    a1             a2         named=3    # [a1, a2], {named: 3}

Named-only with normal named
    Dynamic    named=foo      positional=bar        # [], {positional: bar, named: foo}

Named-only with free named
    Dynamic    named=value    foo=bar               # [], {named: value, foo=bar}
    Dynamic    named2=2       third=3    named=1    # [], {named: 1, named2: 2, third: 3}