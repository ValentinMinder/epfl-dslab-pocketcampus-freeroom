/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.events.shared;

import org.apache.commons.lang.builder.HashCodeBuilder;
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

public class EventItemRequest implements org.apache.thrift.TBase<EventItemRequest, EventItemRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EventItemRequest");

  private static final org.apache.thrift.protocol.TField EVENT_ITEM_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("eventItemId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField USER_TOKEN_FIELD_DESC = new org.apache.thrift.protocol.TField("userToken", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField LANG_FIELD_DESC = new org.apache.thrift.protocol.TField("lang", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField PERIOD_FIELD_DESC = new org.apache.thrift.protocol.TField("period", org.apache.thrift.protocol.TType.I32, (short)6);

  public long eventItemId; // required
  public String userToken; // required
  public String lang; // required
  public int period; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    EVENT_ITEM_ID((short)1, "eventItemId"),
    USER_TOKEN((short)2, "userToken"),
    LANG((short)5, "lang"),
    PERIOD((short)6, "period");

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
        case 1: // EVENT_ITEM_ID
          return EVENT_ITEM_ID;
        case 2: // USER_TOKEN
          return USER_TOKEN;
        case 5: // LANG
          return LANG;
        case 6: // PERIOD
          return PERIOD;
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
  private static final int __EVENTITEMID_ISSET_ID = 0;
  private static final int __PERIOD_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.EVENT_ITEM_ID, new org.apache.thrift.meta_data.FieldMetaData("eventItemId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.USER_TOKEN, new org.apache.thrift.meta_data.FieldMetaData("userToken", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LANG, new org.apache.thrift.meta_data.FieldMetaData("lang", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PERIOD, new org.apache.thrift.meta_data.FieldMetaData("period", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EventItemRequest.class, metaDataMap);
  }

  public EventItemRequest() {
  }

  public EventItemRequest(
    long eventItemId)
  {
    this();
    this.eventItemId = eventItemId;
    setEventItemIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EventItemRequest(EventItemRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.eventItemId = other.eventItemId;
    if (other.isSetUserToken()) {
      this.userToken = other.userToken;
    }
    if (other.isSetLang()) {
      this.lang = other.lang;
    }
    this.period = other.period;
  }

  public EventItemRequest deepCopy() {
    return new EventItemRequest(this);
  }

  @Override
  public void clear() {
    setEventItemIdIsSet(false);
    this.eventItemId = 0;
    this.userToken = null;
    this.lang = null;
    setPeriodIsSet(false);
    this.period = 0;
  }

  public long getEventItemId() {
    return this.eventItemId;
  }

  public EventItemRequest setEventItemId(long eventItemId) {
    this.eventItemId = eventItemId;
    setEventItemIdIsSet(true);
    return this;
  }

  public void unsetEventItemId() {
    __isset_bit_vector.clear(__EVENTITEMID_ISSET_ID);
  }

  /** Returns true if field eventItemId is set (has been assigned a value) and false otherwise */
  public boolean isSetEventItemId() {
    return __isset_bit_vector.get(__EVENTITEMID_ISSET_ID);
  }

  public void setEventItemIdIsSet(boolean value) {
    __isset_bit_vector.set(__EVENTITEMID_ISSET_ID, value);
  }

  public String getUserToken() {
    return this.userToken;
  }

  public EventItemRequest setUserToken(String userToken) {
    this.userToken = userToken;
    return this;
  }

  public void unsetUserToken() {
    this.userToken = null;
  }

  /** Returns true if field userToken is set (has been assigned a value) and false otherwise */
  public boolean isSetUserToken() {
    return this.userToken != null;
  }

  public void setUserTokenIsSet(boolean value) {
    if (!value) {
      this.userToken = null;
    }
  }

  public String getLang() {
    return this.lang;
  }

  public EventItemRequest setLang(String lang) {
    this.lang = lang;
    return this;
  }

  public void unsetLang() {
    this.lang = null;
  }

  /** Returns true if field lang is set (has been assigned a value) and false otherwise */
  public boolean isSetLang() {
    return this.lang != null;
  }

  public void setLangIsSet(boolean value) {
    if (!value) {
      this.lang = null;
    }
  }

  public int getPeriod() {
    return this.period;
  }

  public EventItemRequest setPeriod(int period) {
    this.period = period;
    setPeriodIsSet(true);
    return this;
  }

  public void unsetPeriod() {
    __isset_bit_vector.clear(__PERIOD_ISSET_ID);
  }

  /** Returns true if field period is set (has been assigned a value) and false otherwise */
  public boolean isSetPeriod() {
    return __isset_bit_vector.get(__PERIOD_ISSET_ID);
  }

  public void setPeriodIsSet(boolean value) {
    __isset_bit_vector.set(__PERIOD_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case EVENT_ITEM_ID:
      if (value == null) {
        unsetEventItemId();
      } else {
        setEventItemId((Long)value);
      }
      break;

    case USER_TOKEN:
      if (value == null) {
        unsetUserToken();
      } else {
        setUserToken((String)value);
      }
      break;

    case LANG:
      if (value == null) {
        unsetLang();
      } else {
        setLang((String)value);
      }
      break;

    case PERIOD:
      if (value == null) {
        unsetPeriod();
      } else {
        setPeriod((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case EVENT_ITEM_ID:
      return Long.valueOf(getEventItemId());

    case USER_TOKEN:
      return getUserToken();

    case LANG:
      return getLang();

    case PERIOD:
      return Integer.valueOf(getPeriod());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case EVENT_ITEM_ID:
      return isSetEventItemId();
    case USER_TOKEN:
      return isSetUserToken();
    case LANG:
      return isSetLang();
    case PERIOD:
      return isSetPeriod();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof EventItemRequest)
      return this.equals((EventItemRequest)that);
    return false;
  }

  public boolean equals(EventItemRequest that) {
    if (that == null)
      return false;

    boolean this_present_eventItemId = true;
    boolean that_present_eventItemId = true;
    if (this_present_eventItemId || that_present_eventItemId) {
      if (!(this_present_eventItemId && that_present_eventItemId))
        return false;
      if (this.eventItemId != that.eventItemId)
        return false;
    }

    boolean this_present_userToken = true && this.isSetUserToken();
    boolean that_present_userToken = true && that.isSetUserToken();
    if (this_present_userToken || that_present_userToken) {
      if (!(this_present_userToken && that_present_userToken))
        return false;
      if (!this.userToken.equals(that.userToken))
        return false;
    }

    boolean this_present_lang = true && this.isSetLang();
    boolean that_present_lang = true && that.isSetLang();
    if (this_present_lang || that_present_lang) {
      if (!(this_present_lang && that_present_lang))
        return false;
      if (!this.lang.equals(that.lang))
        return false;
    }

    boolean this_present_period = true && this.isSetPeriod();
    boolean that_present_period = true && that.isSetPeriod();
    if (this_present_period || that_present_period) {
      if (!(this_present_period && that_present_period))
        return false;
      if (this.period != that.period)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_eventItemId = true;
    builder.append(present_eventItemId);
    if (present_eventItemId)
      builder.append(eventItemId);

    boolean present_userToken = true && (isSetUserToken());
    builder.append(present_userToken);
    if (present_userToken)
      builder.append(userToken);

    boolean present_lang = true && (isSetLang());
    builder.append(present_lang);
    if (present_lang)
      builder.append(lang);

    boolean present_period = true && (isSetPeriod());
    builder.append(present_period);
    if (present_period)
      builder.append(period);

    return builder.toHashCode();
  }

  public int compareTo(EventItemRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    EventItemRequest typedOther = (EventItemRequest)other;

    lastComparison = Boolean.valueOf(isSetEventItemId()).compareTo(typedOther.isSetEventItemId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEventItemId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.eventItemId, typedOther.eventItemId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserToken()).compareTo(typedOther.isSetUserToken());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserToken()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userToken, typedOther.userToken);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLang()).compareTo(typedOther.isSetLang());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLang()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.lang, typedOther.lang);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPeriod()).compareTo(typedOther.isSetPeriod());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPeriod()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.period, typedOther.period);
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
        case 1: // EVENT_ITEM_ID
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.eventItemId = iprot.readI64();
            setEventItemIdIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // USER_TOKEN
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.userToken = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // LANG
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.lang = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 6: // PERIOD
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.period = iprot.readI32();
            setPeriodIsSet(true);
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
    if (!isSetEventItemId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'eventItemId' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(EVENT_ITEM_ID_FIELD_DESC);
    oprot.writeI64(this.eventItemId);
    oprot.writeFieldEnd();
    if (this.userToken != null) {
      if (isSetUserToken()) {
        oprot.writeFieldBegin(USER_TOKEN_FIELD_DESC);
        oprot.writeString(this.userToken);
        oprot.writeFieldEnd();
      }
    }
    if (this.lang != null) {
      if (isSetLang()) {
        oprot.writeFieldBegin(LANG_FIELD_DESC);
        oprot.writeString(this.lang);
        oprot.writeFieldEnd();
      }
    }
    if (isSetPeriod()) {
      oprot.writeFieldBegin(PERIOD_FIELD_DESC);
      oprot.writeI32(this.period);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("EventItemRequest(");
    boolean first = true;

    sb.append("eventItemId:");
    sb.append(this.eventItemId);
    first = false;
    if (isSetUserToken()) {
      if (!first) sb.append(", ");
      sb.append("userToken:");
      if (this.userToken == null) {
        sb.append("null");
      } else {
        sb.append(this.userToken);
      }
      first = false;
    }
    if (isSetLang()) {
      if (!first) sb.append(", ");
      sb.append("lang:");
      if (this.lang == null) {
        sb.append("null");
      } else {
        sb.append(this.lang);
      }
      first = false;
    }
    if (isSetPeriod()) {
      if (!first) sb.append(", ");
      sb.append("period:");
      sb.append(this.period);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'eventItemId' because it's a primitive and you chose the non-beans generator.
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

