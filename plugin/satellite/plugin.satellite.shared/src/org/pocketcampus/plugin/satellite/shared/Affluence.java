/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.satellite.shared;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum Affluence implements org.apache.thrift.TEnum {
  EMPTY(0),
  MEDIUM(1),
  CROWDED(2),
  FULL(3),
  CLOSED(4),
  ERROR(5);

  private final int value;

  private Affluence(int value) {
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
  public static Affluence findByValue(int value) { 
    switch (value) {
      case 0:
        return EMPTY;
      case 1:
        return MEDIUM;
      case 2:
        return CROWDED;
      case 3:
        return FULL;
      case 4:
        return CLOSED;
      case 5:
        return ERROR;
      default:
        return null;
    }
  }
}
