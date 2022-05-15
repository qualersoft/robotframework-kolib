*** Settings ***
Documentation   This is the suite for countries eu webservice showcase tests
Resource        ${EXECDIR}/src/test/robots/keywords/countries/countries.resource
Suite setup     Create default country germany
Test Template   Search country by name contains a country

*** Test Cases ***

Search for country by name Ger      Ger         ${CNTRY_GER}
Search for country by name Deutsch  Deutsch     ${CNTRY_GER}
Search for country by name Deu      Deu         ${CNTRY_GER}

Search for country by region Europe
  [Documentation]   Searchs all countries in the european region
  [Template]  None
  ${resp}=  Get countries by region   Europe
  Assert request succeed  ${resp}
  ${countries}=   Get countries from response   ${resp}
  Assert that '${countries}' contains '${CNTRY_GER}'

Search for country by currency EUR
  [Documentation]   Searchs all countries which use the currency euro
  [Template]  None
  ${resp}=  Get countries by currency   EUR
  Assert request succeed  ${resp}
  ${countries}=   Get countries from response   ${resp}
  Assert that '${countries}' contains '${CNTRY_GER}'
