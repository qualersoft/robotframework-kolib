*** Settings ***
Library       KExampleLib
Library       DateTime

*** Test Cases ***    # primitive types
Type conversions string with None
  ${actual}=    TC Call With String    ${None}
  Should Be Equal    ${actual}    Got <null>

Type conversions string with value
  ${actual}=    TC Call With String    string input
  Should Be Equal    ${actual}    Got string input

Type conversion bool with None
  ${actual}=    TC Call With Boolean    ${None}
  Should Be Equal    ${actual}    Got <null>

Type conversion bool with Yes-string-value
  ${actual}=    TC Call With Boolean    Yes
  Should Be Equal    ${actual}    Got true

Type conversion bool with No-string-value
  ${actual}=    TC Call With Boolean    No
  Should Be Equal    ${actual}    Got false

Type conversion bool with true type value
  ${actual}=    TC Call With Boolean    ${True}
  Should Be Equal    ${actual}    Got true

Type conversion bool with false type value
  ${actual}=    TC Call With Boolean    ${False}
  Should Be Equal    ${actual}    Got false

#Type conversion byte with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    TC Call With Byte   ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion byte with value
  ${actual}=    TC Call With Byte   42
  Should Be Equal    ${actual}    Got 42

#Type conversion short with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    Tc Call With Short   ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion short with value
  ${actual}=    Tc Call With Short   42
  Should Be Equal    ${actual}    Got 42

#Type conversion int with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    Tc Call With Int   ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion int with value
  ${actual}=    Tc Call With Int   42
  Should Be Equal    ${actual}    Got 42

#Type conversion long with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    Tc Call With Long   ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion long with value
  ${actual}=    Tc Call With Long   42
  Should Be Equal    ${actual}    Got 42

#Type conversion float with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    Tc Call With Float   ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion float with value
  ${actual}=    Tc Call With Float   42.42
  Should Be Equal    ${actual}    Got 42.42

#Type conversion double with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    Tc Call With Double   ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion double with value
  ${actual}=    Tc Call With Double   42.424242424242429999   # last '2' gets round up
  Should Be Equal    ${actual}    Got 42.42424242424243

*** Test Cases ***    # complex types

Type conversion BigInteger with None
  ${actual}=    Tc Call With Big Integer   ${None}
  Should Be Equal    ${actual}    Got <null>

Type conversion BitInteger with value
  ${actual}=    Tc Call With Big Integer   42424242424242424242424242424242
  Should Be Equal    ${actual}    Got 42424242424242424242424242424242

Type conversion BigDecimal with None
  ${actual}=    Tc Call With Big Decimal   ${None}
  Should Be Equal    ${actual}    Got <null>

Type conversion BigDecimal with value
  ${actual}=    Tc Call With Big Decimal   42.424242424242424242424242424242
  Should Be Equal    ${actual}    Got 42.424242424242424242424242424242

Type conversion Datetime with none
  ${actual}=    Tc Call With Date Time    ${None}
  Should Be Equal    ${actual}    Got <null>

Type conversion date time with string-value
  ${actual}=    Tc Call With Date Time    2442-12-30
  Should Be Equal    ${actual}    Got 2442-12-30T00:00

Type conversion date time with type-value
  ${in}=        Get Current Date
  ${actual}=    Tc Call With Date Time    ${in}
  Should Not Be Equal    ${actual}    Got <null>

#Type conversion delta time with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    Tc Call With Delta Time    ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion delta time with int-value
  ${actual}=    Tc Call With Delta Time    42
  Should Be Equal    ${actual}    Got PT42S

Type conversion delta time with duration string-value
  ${actual}=    Tc Call With Delta Time    1 minute 5 seconds
  Should Be Equal    ${actual}    Got PT1M5S

#Type conversion bytearray with None
#  [Documentation]   Not working as not supported by robot/python
#  ${actual}=    Tc Call With Byte Array    ${None}
#  Should Be Equal    ${actual}    Got <null>

Type conversion bytearray with value
  ${actual}=    Tc Call With Byte Array    hyvä
  Should Be Equal    ${actual}    Got [104,121,118,-28]
