/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.events.shared;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum EventsPeriods implements org.apache.thrift.TEnum {
  ONE_DAY(1),
  TWO_DAYS(2),
  ONE_WEEK(7),
  TWO_WEEKS(14),
  ONE_MONTH(30),
  SIX_MONTHS(180),
  ONE_YEAR(365);

  private final int value;

  private EventsPeriods(int value) {
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
  public static EventsPeriods findByValue(int value) { 
    switch (value) {
      case 1:
        return ONE_DAY;
      case 2:
        return TWO_DAYS;
      case 7:
        return ONE_WEEK;
      case 14:
        return TWO_WEEKS;
      case 30:
        return ONE_MONTH;
      case 180:
        return SIX_MONTHS;
      case 365:
        return ONE_YEAR;
      default:
        return null;
    }
  }
}
