/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.freeroom.shared;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum FRLanguage implements org.apache.thrift.TEnum {
  EN(0),
  FR(1);

  private final int value;

  private FRLanguage(int value) {
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
  public static FRLanguage findByValue(int value) { 
    switch (value) {
      case 0:
        return EN;
      case 1:
        return FR;
      default:
        return null;
    }
  }
}
