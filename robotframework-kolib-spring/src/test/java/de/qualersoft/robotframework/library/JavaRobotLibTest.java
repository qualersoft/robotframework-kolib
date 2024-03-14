package de.qualersoft.robotframework.library;

import de.qualersoft.robotframework.jdummy.JDummyLib;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaRobotLibTest {

  @Test
  void shouldFindSimpleKeyword() {
    var actual = getPckgKwdNames();
    assertTrue(actual.contains("getSimpleJ"));
  }

  @Test
  void shouldFindComplexKeyword() {
    var actual = getPckgKwdNames();
    assertTrue(actual.contains("getConfigDummyJ"));
  }

  @Test
  void shouldExecWithValueFromConfigYml() {
    var actual = runPckgKwd("getConfigDummyJ");
    assertInstanceOf(String.class, actual);
    assertEquals("Hello from im a dummy", (String)actual);
  }

  private static List<String> getPckgKwdNames() {
    return getRobotLib().getKeywordNames();
  }

  private static Object runPckgKwd(final String name) {
    return getRobotLib().runKeyword(name, Collections.emptyList(), Collections.emptyMap());
  }

  private static RobotLib getRobotLib() {
    return new JDummyLib();
  }
}
