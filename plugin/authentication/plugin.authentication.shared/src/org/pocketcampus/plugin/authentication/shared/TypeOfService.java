/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.authentication.shared;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum TypeOfService implements org.apache.thrift.TEnum {
  SERVICE_POCKETCAMPUS(0),
  SERVICE_MOODLE(1),
  SERVICE_CAMIPRO(2),
  SERVICE_ISA(3);

  private final int value;

  private TypeOfService(int value) {
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
  public static TypeOfService findByValue(int value) { 
    switch (value) {
      case 0:
        return SERVICE_POCKETCAMPUS;
      case 1:
        return SERVICE_MOODLE;
      case 2:
        return SERVICE_CAMIPRO;
      case 3:
        return SERVICE_ISA;
      default:
        return null;
    }
  }
}
