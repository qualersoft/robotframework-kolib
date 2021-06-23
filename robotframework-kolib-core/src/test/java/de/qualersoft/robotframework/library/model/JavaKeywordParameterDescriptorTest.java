package de.qualersoft.robotframework.library.model;

import de.qualersoft.robotframework.library.annotation.KwdArg;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KCallable;
import kotlin.reflect.KParameter;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class JavaKeywordParameterDescriptorTest {

  @Test
  public void testPlainParameter() {
    var param = getParameterOf(PlainFunctionsHolderClass.class, "plainParameter");
    var desc = new KeywordParameterDescriptor(param);
    assertAll(
        () -> assertEquals("arg0", desc.getName())
    );
  }

  @Test
  public void testWithParameterNameByAnnotation() {
    var param = getParameterOf(AnnotatedFunctionsHolderClass.class, "withParameterNameByAnnotation");
    var desc = new KeywordParameterDescriptor(param);
    assertAll(
        () -> assertEquals("test", desc.getName())
    );
  }

  @Test
  public void testWithEmptyParameterNameByAnnotation() {
    var param = getParameterOf(AnnotatedFunctionsHolderClass.class, "withEmptyParameterNameByAnnotation");
    var desc = new KeywordParameterDescriptor(param);
    assertAll(
        () -> assertEquals("arg0", desc.getName())
    );
  }

  @Test
  public void testWithBlankParameterNameByAnnotation() {
    var param = getParameterOf(AnnotatedFunctionsHolderClass.class, "withBlankParameterNameByAnnotation");
    var desc = new KeywordParameterDescriptor(param);
    assertAll(
        () -> assertEquals("arg0", desc.getName())
    );
  }

  @Test
  public void testWithSurroundingBlanksParameterNameByAnnotation() {
    var param = getParameterOf(AnnotatedFunctionsHolderClass.class, "withSurroundingBlanksParameterNameByAnnotation");
    var desc = new KeywordParameterDescriptor(param);
    assertAll(
        () -> assertEquals("test", desc.getName())
    );
  }

  private KParameter getParameterOf(Class<?> clazz, String method) {
    var kclass = Reflection.createKotlinClass(clazz);
    @SuppressWarnings("unchecked")
    Collection<KCallable<?>> members = kclass.getMembers();
    var kFunction = members.stream()
        .filter(it -> method.equals(it.getName())).findFirst();
    assertTrue(kFunction.isPresent());
    return kFunction.get().getParameters().get(1);
  }

  @SuppressWarnings("unused")
  static class PlainFunctionsHolderClass {
    public void plainParameter(String test) {
    }
  }

  @SuppressWarnings("unused")
  static class AnnotatedFunctionsHolderClass {
    public void withParameterNameByAnnotation(@KwdArg(name = "test") String a) {
    }

    public void withEmptyParameterNameByAnnotation(@KwdArg(name = "") String a) {
    }

    public void withBlankParameterNameByAnnotation(@KwdArg(name = " ") String a) {
    }

    public void withSurroundingBlanksParameterNameByAnnotation(@KwdArg(name = " test ") String a) {
    }
  }
}
