package de.qualersoft.robotframework.library.example.keywords.functions;

import de.qualersoft.robotframework.library.annotation.Keyword;
import de.qualersoft.robotframework.library.annotation.KwdArg;
import de.qualersoft.robotframework.library.model.ParameterKind;
import jakarta.inject.Named;

import java.util.List;
import java.util.Map;

@Named
public class FunctionKwds {

  @Keyword
  public void fnCallWithVarargs(
      String posArg,
      @KwdArg(kind = ParameterKind.VARARG)
      List<String> varargs
  ) {
    System.out.printf("Mandatory posArg: %s%n", posArg);

    if (varargs.isEmpty()) {
      System.out.println("No varargs passed.");
    } else {
      System.out.printf("Got varargs: %s%n", varargs);
    }
  }

  @Keyword
  public void fnCallWithKwargs(
      String posArg,
      @KwdArg(kind = ParameterKind.KWARG)
      Map<String, String> kwargs
  ) {
    System.out.printf("Mandatory posArg: %s%n", posArg);

    if (kwargs.isEmpty()) {
      System.out.println("No kwargs passed.");
    } else {
      System.out.printf("Got kwargs: %s%n", kwargs);
    }
  }

  @Keyword
  public void fnCallWithVarAndKwArgs(
      String posArg,
      @KwdArg(kind = ParameterKind.VARARG)
      List<String> varargs,
      @KwdArg(kind = ParameterKind.KWARG)
      Map<String, String> kwargs
  ) {
    System.out.printf("Mandatory posArg: %s%n", posArg);

    if (varargs.isEmpty()) {
      System.out.println("No varargs passed.");
    } else {
      System.out.printf("Got varargs: %s%n", varargs);
    }

    if (kwargs.isEmpty()) {
      System.out.println("No kwargs passed.");
    } else {
      System.out.printf("Got kwargs: %s%n", kwargs);
    }
  }
}
