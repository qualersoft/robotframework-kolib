*** Settings ***
Test template    Log test case name

*** Variables ***
${TCNumber}=            1

*** Test Cases ***
My test case ${TCNumber}      ${TCNumber}
  [Tags]   TID: Test1


*** Keywords ***
Log test case name
  [Arguments]   ${tcn}
  Log    I'm the test case number ${tcn}
