package de.qualersoft.robotframework.library.example.keywords.functions;

import de.qualersoft.robotframework.library.annotation.Keyword;

import jakarta.inject.Named;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named
public class TypeConversionKwds {

  //<editor-fold desc="Description">
  @Keyword
  public String tcCallWithString(String arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithBoolean(Boolean arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithByte(Byte arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithShort(Short arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithInt(Integer arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithLong(Long arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithFloat(Float arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithDouble(Double arg) {
    return createResult(arg);
  }
  //</editor-fold>

  //<editor-fold desc="Complex types">
  @Keyword
  public String tcCallWithBigInteger(BigInteger arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithBigDecimal(BigDecimal arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithDateTime(LocalDateTime arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithDeltaTime(Duration arg) {
    return createResult(arg);
  }

  @Keyword
  public String tcCallWithByteArray(CharSequence arg) {
    return createResult(arg, arr ->
        arg.chars().mapToObj(it -> Integer.valueOf(it).byteValue()).map(Object::toString).collect(Collectors.joining(", ", "Got [", "]"))
    );
  }
  //</editor-fold>

  private <T> String createResult(T arg) {
    return createResult(arg, null);
  }

  private <T> String createResult(T arg, Function<T, String> otherWise) {
    if (null == arg) {
      return "Got <null>";
    } else if (null == otherWise) {
      return String.format("Got %s", arg);
    } else {
      return otherWise.apply(arg);
    }
  }
}
