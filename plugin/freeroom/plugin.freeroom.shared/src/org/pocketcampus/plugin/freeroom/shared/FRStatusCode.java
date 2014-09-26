/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.freeroom.shared;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum FRStatusCode implements org.apache.thrift.TEnum {
  HTTP_UPDATED(299),
  HTTP_OK(200),
  HTTP_BAD_REQUEST(400),
  HTTP_INTERNAL_ERROR(500),
  HTTP_CONFLICT(409),
  HTTP_PRECON_FAILED(412);

  private final int value;

  private FRStatusCode(int value) {
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
  public static FRStatusCode findByValue(int value) { 
    switch (value) {
      case 299:
        return HTTP_UPDATED;
      case 200:
        return HTTP_OK;
      case 400:
        return HTTP_BAD_REQUEST;
      case 500:
        return HTTP_INTERNAL_ERROR;
      case 409:
        return HTTP_CONFLICT;
      case 412:
        return HTTP_PRECON_FAILED;
      default:
        return null;
    }
  }
}
