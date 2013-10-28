/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.food.shared;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoodRequest implements org.apache.thrift.TBase<FoodRequest, FoodRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FoodRequest");

  private static final org.apache.thrift.protocol.TField DEVICE_LANGUAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("deviceLanguage", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField MEAL_TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("mealTime", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField MEAL_DATE_FIELD_DESC = new org.apache.thrift.protocol.TField("mealDate", org.apache.thrift.protocol.TType.I64, (short)3);
  private static final org.apache.thrift.protocol.TField DEVICE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("deviceId", org.apache.thrift.protocol.TType.STRING, (short)4);

  private String deviceLanguage; // required
  private MealTime mealTime; // required
  private long mealDate; // required
  private String deviceId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    DEVICE_LANGUAGE((short)1, "deviceLanguage"),
    /**
     * 
     * @see MealTime
     */
    MEAL_TIME((short)2, "mealTime"),
    MEAL_DATE((short)3, "mealDate"),
    DEVICE_ID((short)4, "deviceId");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // DEVICE_LANGUAGE
          return DEVICE_LANGUAGE;
        case 2: // MEAL_TIME
          return MEAL_TIME;
        case 3: // MEAL_DATE
          return MEAL_DATE;
        case 4: // DEVICE_ID
          return DEVICE_ID;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __MEALDATE_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.DEVICE_LANGUAGE, new org.apache.thrift.meta_data.FieldMetaData("deviceLanguage", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.MEAL_TIME, new org.apache.thrift.meta_data.FieldMetaData("mealTime", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, MealTime.class)));
    tmpMap.put(_Fields.MEAL_DATE, new org.apache.thrift.meta_data.FieldMetaData("mealDate", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.DEVICE_ID, new org.apache.thrift.meta_data.FieldMetaData("deviceId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FoodRequest.class, metaDataMap);
  }

  public FoodRequest() {
    this.deviceLanguage = "fr";

    this.mealTime = org.pocketcampus.plugin.food.shared.MealTime.LUNCH;

    this.mealDate = -1L;

  }

  public FoodRequest(
    String deviceLanguage,
    MealTime mealTime,
    long mealDate)
  {
    this();
    this.deviceLanguage = deviceLanguage;
    this.mealTime = mealTime;
    this.mealDate = mealDate;
    setMealDateIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FoodRequest(FoodRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetDeviceLanguage()) {
      this.deviceLanguage = other.deviceLanguage;
    }
    if (other.isSetMealTime()) {
      this.mealTime = other.mealTime;
    }
    this.mealDate = other.mealDate;
    if (other.isSetDeviceId()) {
      this.deviceId = other.deviceId;
    }
  }

  public FoodRequest deepCopy() {
    return new FoodRequest(this);
  }

  @Override
  public void clear() {
    this.deviceLanguage = "fr";

    this.mealTime = org.pocketcampus.plugin.food.shared.MealTime.LUNCH;

    this.mealDate = -1L;

    this.deviceId = null;
  }

  public String getDeviceLanguage() {
    return this.deviceLanguage;
  }

  public FoodRequest setDeviceLanguage(String deviceLanguage) {
    this.deviceLanguage = deviceLanguage;
    return this;
  }

  public void unsetDeviceLanguage() {
    this.deviceLanguage = null;
  }

  /** Returns true if field deviceLanguage is set (has been assigned a value) and false otherwise */
  public boolean isSetDeviceLanguage() {
    return this.deviceLanguage != null;
  }

  public void setDeviceLanguageIsSet(boolean value) {
    if (!value) {
      this.deviceLanguage = null;
    }
  }

  /**
   * 
   * @see MealTime
   */
  public MealTime getMealTime() {
    return this.mealTime;
  }

  /**
   * 
   * @see MealTime
   */
  public FoodRequest setMealTime(MealTime mealTime) {
    this.mealTime = mealTime;
    return this;
  }

  public void unsetMealTime() {
    this.mealTime = null;
  }

  /** Returns true if field mealTime is set (has been assigned a value) and false otherwise */
  public boolean isSetMealTime() {
    return this.mealTime != null;
  }

  public void setMealTimeIsSet(boolean value) {
    if (!value) {
      this.mealTime = null;
    }
  }

  public long getMealDate() {
    return this.mealDate;
  }

  public FoodRequest setMealDate(long mealDate) {
    this.mealDate = mealDate;
    setMealDateIsSet(true);
    return this;
  }

  public void unsetMealDate() {
    __isset_bit_vector.clear(__MEALDATE_ISSET_ID);
  }

  /** Returns true if field mealDate is set (has been assigned a value) and false otherwise */
  public boolean isSetMealDate() {
    return __isset_bit_vector.get(__MEALDATE_ISSET_ID);
  }

  public void setMealDateIsSet(boolean value) {
    __isset_bit_vector.set(__MEALDATE_ISSET_ID, value);
  }

  public String getDeviceId() {
    return this.deviceId;
  }

  public FoodRequest setDeviceId(String deviceId) {
    this.deviceId = deviceId;
    return this;
  }

  public void unsetDeviceId() {
    this.deviceId = null;
  }

  /** Returns true if field deviceId is set (has been assigned a value) and false otherwise */
  public boolean isSetDeviceId() {
    return this.deviceId != null;
  }

  public void setDeviceIdIsSet(boolean value) {
    if (!value) {
      this.deviceId = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case DEVICE_LANGUAGE:
      if (value == null) {
        unsetDeviceLanguage();
      } else {
        setDeviceLanguage((String)value);
      }
      break;

    case MEAL_TIME:
      if (value == null) {
        unsetMealTime();
      } else {
        setMealTime((MealTime)value);
      }
      break;

    case MEAL_DATE:
      if (value == null) {
        unsetMealDate();
      } else {
        setMealDate((Long)value);
      }
      break;

    case DEVICE_ID:
      if (value == null) {
        unsetDeviceId();
      } else {
        setDeviceId((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case DEVICE_LANGUAGE:
      return getDeviceLanguage();

    case MEAL_TIME:
      return getMealTime();

    case MEAL_DATE:
      return Long.valueOf(getMealDate());

    case DEVICE_ID:
      return getDeviceId();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case DEVICE_LANGUAGE:
      return isSetDeviceLanguage();
    case MEAL_TIME:
      return isSetMealTime();
    case MEAL_DATE:
      return isSetMealDate();
    case DEVICE_ID:
      return isSetDeviceId();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof FoodRequest)
      return this.equals((FoodRequest)that);
    return false;
  }

  public boolean equals(FoodRequest that) {
    if (that == null)
      return false;

    boolean this_present_deviceLanguage = true && this.isSetDeviceLanguage();
    boolean that_present_deviceLanguage = true && that.isSetDeviceLanguage();
    if (this_present_deviceLanguage || that_present_deviceLanguage) {
      if (!(this_present_deviceLanguage && that_present_deviceLanguage))
        return false;
      if (!this.deviceLanguage.equals(that.deviceLanguage))
        return false;
    }

    boolean this_present_mealTime = true && this.isSetMealTime();
    boolean that_present_mealTime = true && that.isSetMealTime();
    if (this_present_mealTime || that_present_mealTime) {
      if (!(this_present_mealTime && that_present_mealTime))
        return false;
      if (!this.mealTime.equals(that.mealTime))
        return false;
    }

    boolean this_present_mealDate = true;
    boolean that_present_mealDate = true;
    if (this_present_mealDate || that_present_mealDate) {
      if (!(this_present_mealDate && that_present_mealDate))
        return false;
      if (this.mealDate != that.mealDate)
        return false;
    }

    boolean this_present_deviceId = true && this.isSetDeviceId();
    boolean that_present_deviceId = true && that.isSetDeviceId();
    if (this_present_deviceId || that_present_deviceId) {
      if (!(this_present_deviceId && that_present_deviceId))
        return false;
      if (!this.deviceId.equals(that.deviceId))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(FoodRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    FoodRequest typedOther = (FoodRequest)other;

    lastComparison = Boolean.valueOf(isSetDeviceLanguage()).compareTo(typedOther.isSetDeviceLanguage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDeviceLanguage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.deviceLanguage, typedOther.deviceLanguage);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMealTime()).compareTo(typedOther.isSetMealTime());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMealTime()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mealTime, typedOther.mealTime);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMealDate()).compareTo(typedOther.isSetMealDate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMealDate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mealDate, typedOther.mealDate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDeviceId()).compareTo(typedOther.isSetDeviceId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDeviceId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.deviceId, typedOther.deviceId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    org.apache.thrift.protocol.TField field;
    iprot.readStructBegin();
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == org.apache.thrift.protocol.TType.STOP) { 
        break;
      }
      switch (field.id) {
        case 1: // DEVICE_LANGUAGE
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.deviceLanguage = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // MEAL_TIME
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.mealTime = MealTime.findByValue(iprot.readI32());
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // MEAL_DATE
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.mealDate = iprot.readI64();
            setMealDateIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // DEVICE_ID
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.deviceId = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        default:
          org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
      }
      iprot.readFieldEnd();
    }
    iprot.readStructEnd();

    // check for required fields of primitive type, which can't be checked in the validate method
    if (!isSetMealDate()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mealDate' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.deviceLanguage != null) {
      oprot.writeFieldBegin(DEVICE_LANGUAGE_FIELD_DESC);
      oprot.writeString(this.deviceLanguage);
      oprot.writeFieldEnd();
    }
    if (this.mealTime != null) {
      oprot.writeFieldBegin(MEAL_TIME_FIELD_DESC);
      oprot.writeI32(this.mealTime.getValue());
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(MEAL_DATE_FIELD_DESC);
    oprot.writeI64(this.mealDate);
    oprot.writeFieldEnd();
    if (this.deviceId != null) {
      if (isSetDeviceId()) {
        oprot.writeFieldBegin(DEVICE_ID_FIELD_DESC);
        oprot.writeString(this.deviceId);
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("FoodRequest(");
    boolean first = true;

    sb.append("deviceLanguage:");
    if (this.deviceLanguage == null) {
      sb.append("null");
    } else {
      sb.append(this.deviceLanguage);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("mealTime:");
    if (this.mealTime == null) {
      sb.append("null");
    } else {
      sb.append(this.mealTime);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("mealDate:");
    sb.append(this.mealDate);
    first = false;
    if (isSetDeviceId()) {
      if (!first) sb.append(", ");
      sb.append("deviceId:");
      if (this.deviceId == null) {
        sb.append("null");
      } else {
        sb.append(this.deviceId);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (deviceLanguage == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'deviceLanguage' was not present! Struct: " + toString());
    }
    if (mealTime == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mealTime' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'mealDate' because it's a primitive and you chose the non-beans generator.
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

}

