/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.transport.shared;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum Type implements org.apache.thrift.TEnum {
  ADULT(0),
  CHILD(1),
  YOUTH(2),
  STUDENT(3),
  MILITARY(4),
  SENIOR(5),
  DISABLED(6);

  private final int value;

  private Type(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static Type findByValue(int value) { 
    switch (value) {
      case 0:
        return ADULT;
      case 1:
        return CHILD;
      case 2:
        return YOUTH;
      case 3:
        return STUDENT;
      case 4:
        return MILITARY;
      case 5:
        return SENIOR;
      case 6:
        return DISABLED;
      default:
        return null;
    }
  }
}
