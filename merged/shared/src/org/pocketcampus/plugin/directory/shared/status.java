/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.directory.shared;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum status implements org.apache.thrift.TEnum {
  STUDENT(0),
  PHD_CANDIDATE(1),
  PROFESSOR(2),
  COWORKER(3);

  private final int value;

  private status(int value) {
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
  public static status findByValue(int value) { 
    switch (value) {
      case 0:
        return STUDENT;
      case 1:
        return PHD_CANDIDATE;
      case 2:
        return PROFESSOR;
      case 3:
        return COWORKER;
      default:
        return null;
    }
  }
}
