package de.ugoe.cs.smartshark.refshark.util;

import refdiff.core.api.RefactoringType;

/**
 * A collection of common used helper methods.
 *
 * @author <a href="mailto:dhonsel@informatik.uni-goettingen.de">Daniel Honsel</a>
 */
public class Common {

  /**
   * This method compares the JVM method signature provided by SourceMeter withe the
   * simple method name provided by RefDiff. If both names represents the same method,
   * this method returns true.
   * @param mSourceMeter The JVM method signature provided by SourceMeter.
   * @param mRefDiff The simple method signature provided by RefDiff.
   * @return If both names represents the same method, his method returns true.
   * Otherwise false.
   */
  public static boolean compareMethods(String mSourceMeter, String mRefDiff) {
    // simplify SourceMeter name
    String name = mSourceMeter.split("\\(")[0];
    String signature = mSourceMeter.split("\\(")[1].split("\\)")[0];
    String simpleSignature = simplifyJavaVMMethodSignature(signature);
    String sm = (name + simpleSignature).replace(" ", "");

    String rd = mRefDiff.replace(" ", "");

    return sm.equals(rd);
  }

  /*
   * Simplifies a JVM signature. It cuts path information of passed types and
   * reformats array brackets.
   */
  private static String simplifyJavaVMMethodSignature(String signature) {
    StringBuilder simpleName = new StringBuilder();
    for (int i = 0; i < signature.length(); ++i) {
      StringBuilder type = new StringBuilder();
      if (signature.charAt(i) == '[') {
        if (signature.charAt(i + 1) == 'L') {
          int j = getTypeName(type, signature, i + 2);
          i = j;
          type.append("[]");
        } else {
          type.append(getJVMPrimitiveType(signature.charAt(i + 1)));
          type.append("[]");
          i += 1;
        }
      } else if (signature.charAt(i) == 'L') {
        int j = getTypeName(type, signature, i + 1);
        i = j;
      } else {
        type.append(getJVMPrimitiveType(signature.charAt(i)));
      }
      if (simpleName.length() == 0) {
        simpleName.append("(");
        simpleName.append(type);
      } else {
        simpleName.append(", ");
        simpleName.append(type);
      }
    }
    if (simpleName.length() == 0) {
      simpleName.append("(");
    }
    simpleName.append(")");
    return simpleName.toString();
  }

  private static int getTypeName(StringBuilder type, String name, int start) {
    String typeName = "";
    for (int j = start; j < name.length(); j++) {
      if (name.charAt(j) == ';') {
        type.append(simpleTypeName(typeName));
        return j;
      } else {
        typeName += name.charAt(j);
      }
    }
    return start + 1;
  }

  private static String getJVMPrimitiveType(char type) {
    switch (type) {
      case 'Z':
        return "boolean";
      case 'B':
        return "byte";
      case 'C':
        return "char";
      case 'S':
        return "short";
      case 'I':
        return "int";
      case 'J':
        return "long";
      case 'F':
        return "float";
      case 'D':
        return "double";
      case 'V':
        return "void";
      default:
        return "";
    }
  }

  private static String simpleTypeName(String name) {
    String[] parts = name.split("/");
    return parts[parts.length - 1];
  }

  /**
   * Returns a string representation of a given refactoring type.
   * @param type The refactoring type.
   * @return The string representation of the given refactoring type.
   */
  public static String refactoringTypeToString(RefactoringType type) {
    switch (type) {
      case EXTRACT_OPERATION:
        return "extract_method";
      case RENAME_CLASS:
        return "rename_class";
      case MOVE_ATTRIBUTE:
        return "move_attribute";
      case RENAME_METHOD:
        return "rename_method";
      case INLINE_OPERATION:
        return "inline_method";
      case MOVE_OPERATION:
        return "move_method";
      case PULL_UP_OPERATION:
        return "pull_up_method";
      case MOVE_CLASS:
        return "move_class";
      case MOVE_RENAME_CLASS:
        return "move_and_rename_class";
      case MOVE_CLASS_FOLDER:
        return "move_class_to_folder";
      case PULL_UP_ATTRIBUTE:
        return "pull_up_attribute";
      case PUSH_DOWN_ATTRIBUTE:
        return "push_down_attribute";
      case PUSH_DOWN_OPERATION:
        return "push_down_method";
      case EXTRACT_INTERFACE:
        return "extract_interface";
      case EXTRACT_SUPERCLASS:
        return "extract_superclass";
      case MERGE_OPERATION:
        return "merge_method";
      case EXTRACT_AND_MOVE_OPERATION:
        return "extract_and_move_method";
      case CONVERT_ANONYMOUS_CLASS_TO_TYPE:
        return "convert_anonymous_class_to_type";
      case INTRODUCE_POLYMORPHISM:
        return "introduce_polymorphism";
      case RENAME_PACKAGE:
        return "rename_package";
      case CHANGE_METHOD_SIGNATURE:
        return "change_method_signature";
      default:
        return "unknown";
    }
  }

  /**
   * Reformats the type name with a '$' as separator for nested types.
   * @param name The type name to be reformatted.
   * @param level The nesting level.
   * @return The reformatted type name with a '$' as separator for nested types.
   */
  public static String formatNestedTypeName(String name, int level) {
    String parts[] = name.split("\\.");
    StringBuilder formattedName = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      formattedName.append(parts[i]);
      if (i < parts.length - (1 + level)) {
        formattedName.append(".");
      } else if (i < parts.length - 1) {
        formattedName.append("$");
      }
    }
    return formattedName.toString();
  }
}
