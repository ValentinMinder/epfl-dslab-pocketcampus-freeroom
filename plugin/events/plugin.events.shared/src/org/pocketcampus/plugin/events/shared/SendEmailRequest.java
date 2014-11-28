/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.pocketcampus.plugin.events.shared;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
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
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2014-11-28")
public class SendEmailRequest implements org.apache.thrift.TBase<SendEmailRequest, SendEmailRequest._Fields>, java.io.Serializable, Cloneable, Comparable<SendEmailRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SendEmailRequest");

  private static final org.apache.thrift.protocol.TField EVENT_POOL_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("eventPoolId", org.apache.thrift.protocol.TType.I64, (short)4);
  private static final org.apache.thrift.protocol.TField STARRED_EVENT_ITEMS_FIELD_DESC = new org.apache.thrift.protocol.TField("starredEventItems", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField USER_TICKETS_FIELD_DESC = new org.apache.thrift.protocol.TField("userTickets", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField EMAIL_ADDRESS_FIELD_DESC = new org.apache.thrift.protocol.TField("emailAddress", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField LANG_FIELD_DESC = new org.apache.thrift.protocol.TField("lang", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new SendEmailRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new SendEmailRequestTupleSchemeFactory());
  }

  private long eventPoolId; // required
  private List<Long> starredEventItems; // required
  private List<String> userTickets; // optional
  private String emailAddress; // optional
  private String lang; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    EVENT_POOL_ID((short)4, "eventPoolId"),
    STARRED_EVENT_ITEMS((short)1, "starredEventItems"),
    USER_TICKETS((short)2, "userTickets"),
    EMAIL_ADDRESS((short)3, "emailAddress"),
    LANG((short)5, "lang");

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
        case 4: // EVENT_POOL_ID
          return EVENT_POOL_ID;
        case 1: // STARRED_EVENT_ITEMS
          return STARRED_EVENT_ITEMS;
        case 2: // USER_TICKETS
          return USER_TICKETS;
        case 3: // EMAIL_ADDRESS
          return EMAIL_ADDRESS;
        case 5: // LANG
          return LANG;
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
  private static final int __EVENTPOOLID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.USER_TICKETS,_Fields.EMAIL_ADDRESS,_Fields.LANG};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.EVENT_POOL_ID, new org.apache.thrift.meta_data.FieldMetaData("eventPoolId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.STARRED_EVENT_ITEMS, new org.apache.thrift.meta_data.FieldMetaData("starredEventItems", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64))));
    tmpMap.put(_Fields.USER_TICKETS, new org.apache.thrift.meta_data.FieldMetaData("userTickets", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.EMAIL_ADDRESS, new org.apache.thrift.meta_data.FieldMetaData("emailAddress", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.LANG, new org.apache.thrift.meta_data.FieldMetaData("lang", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SendEmailRequest.class, metaDataMap);
  }

  public SendEmailRequest() {
  }

  public SendEmailRequest(
    long eventPoolId,
    List<Long> starredEventItems)
  {
    this();
    this.eventPoolId = eventPoolId;
    setEventPoolIdIsSet(true);
    this.starredEventItems = starredEventItems;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SendEmailRequest(SendEmailRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    this.eventPoolId = other.eventPoolId;
    if (other.isSetStarredEventItems()) {
      List<Long> __this__starredEventItems = new ArrayList<Long>(other.starredEventItems);
      this.starredEventItems = __this__starredEventItems;
    }
    if (other.isSetUserTickets()) {
      List<String> __this__userTickets = new ArrayList<String>(other.userTickets);
      this.userTickets = __this__userTickets;
    }
    if (other.isSetEmailAddress()) {
      this.emailAddress = other.emailAddress;
    }
    if (other.isSetLang()) {
      this.lang = other.lang;
    }
  }

  public SendEmailRequest deepCopy() {
    return new SendEmailRequest(this);
  }

  @Override
  public void clear() {
    setEventPoolIdIsSet(false);
    this.eventPoolId = 0;
    this.starredEventItems = null;
    this.userTickets = null;
    this.emailAddress = null;
    this.lang = null;
  }

  public long getEventPoolId() {
    return this.eventPoolId;
  }

  public SendEmailRequest setEventPoolId(long eventPoolId) {
    this.eventPoolId = eventPoolId;
    setEventPoolIdIsSet(true);
    return this;
  }

  public void unsetEventPoolId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __EVENTPOOLID_ISSET_ID);
  }

  /** Returns true if field eventPoolId is set (has been assigned a value) and false otherwise */
  public boolean isSetEventPoolId() {
    return EncodingUtils.testBit(__isset_bitfield, __EVENTPOOLID_ISSET_ID);
  }

  public void setEventPoolIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __EVENTPOOLID_ISSET_ID, value);
  }

  public int getStarredEventItemsSize() {
    return (this.starredEventItems == null) ? 0 : this.starredEventItems.size();
  }

  public java.util.Iterator<Long> getStarredEventItemsIterator() {
    return (this.starredEventItems == null) ? null : this.starredEventItems.iterator();
  }

  public void addToStarredEventItems(long elem) {
    if (this.starredEventItems == null) {
      this.starredEventItems = new ArrayList<Long>();
    }
    this.starredEventItems.add(elem);
  }

  public List<Long> getStarredEventItems() {
    return this.starredEventItems;
  }

  public SendEmailRequest setStarredEventItems(List<Long> starredEventItems) {
    this.starredEventItems = starredEventItems;
    return this;
  }

  public void unsetStarredEventItems() {
    this.starredEventItems = null;
  }

  /** Returns true if field starredEventItems is set (has been assigned a value) and false otherwise */
  public boolean isSetStarredEventItems() {
    return this.starredEventItems != null;
  }

  public void setStarredEventItemsIsSet(boolean value) {
    if (!value) {
      this.starredEventItems = null;
    }
  }

  public int getUserTicketsSize() {
    return (this.userTickets == null) ? 0 : this.userTickets.size();
  }

  public java.util.Iterator<String> getUserTicketsIterator() {
    return (this.userTickets == null) ? null : this.userTickets.iterator();
  }

  public void addToUserTickets(String elem) {
    if (this.userTickets == null) {
      this.userTickets = new ArrayList<String>();
    }
    this.userTickets.add(elem);
  }

  public List<String> getUserTickets() {
    return this.userTickets;
  }

  public SendEmailRequest setUserTickets(List<String> userTickets) {
    this.userTickets = userTickets;
    return this;
  }

  public void unsetUserTickets() {
    this.userTickets = null;
  }

  /** Returns true if field userTickets is set (has been assigned a value) and false otherwise */
  public boolean isSetUserTickets() {
    return this.userTickets != null;
  }

  public void setUserTicketsIsSet(boolean value) {
    if (!value) {
      this.userTickets = null;
    }
  }

  public String getEmailAddress() {
    return this.emailAddress;
  }

  public SendEmailRequest setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  public void unsetEmailAddress() {
    this.emailAddress = null;
  }

  /** Returns true if field emailAddress is set (has been assigned a value) and false otherwise */
  public boolean isSetEmailAddress() {
    return this.emailAddress != null;
  }

  public void setEmailAddressIsSet(boolean value) {
    if (!value) {
      this.emailAddress = null;
    }
  }

  public String getLang() {
    return this.lang;
  }

  public SendEmailRequest setLang(String lang) {
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

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case EVENT_POOL_ID:
      if (value == null) {
        unsetEventPoolId();
      } else {
        setEventPoolId((Long)value);
      }
      break;

    case STARRED_EVENT_ITEMS:
      if (value == null) {
        unsetStarredEventItems();
      } else {
        setStarredEventItems((List<Long>)value);
      }
      break;

    case USER_TICKETS:
      if (value == null) {
        unsetUserTickets();
      } else {
        setUserTickets((List<String>)value);
      }
      break;

    case EMAIL_ADDRESS:
      if (value == null) {
        unsetEmailAddress();
      } else {
        setEmailAddress((String)value);
      }
      break;

    case LANG:
      if (value == null) {
        unsetLang();
      } else {
        setLang((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case EVENT_POOL_ID:
      return Long.valueOf(getEventPoolId());

    case STARRED_EVENT_ITEMS:
      return getStarredEventItems();

    case USER_TICKETS:
      return getUserTickets();

    case EMAIL_ADDRESS:
      return getEmailAddress();

    case LANG:
      return getLang();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case EVENT_POOL_ID:
      return isSetEventPoolId();
    case STARRED_EVENT_ITEMS:
      return isSetStarredEventItems();
    case USER_TICKETS:
      return isSetUserTickets();
    case EMAIL_ADDRESS:
      return isSetEmailAddress();
    case LANG:
      return isSetLang();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SendEmailRequest)
      return this.equals((SendEmailRequest)that);
    return false;
  }

  public boolean equals(SendEmailRequest that) {
    if (that == null)
      return false;

    boolean this_present_eventPoolId = true;
    boolean that_present_eventPoolId = true;
    if (this_present_eventPoolId || that_present_eventPoolId) {
      if (!(this_present_eventPoolId && that_present_eventPoolId))
        return false;
      if (this.eventPoolId != that.eventPoolId)
        return false;
    }

    boolean this_present_starredEventItems = true && this.isSetStarredEventItems();
    boolean that_present_starredEventItems = true && that.isSetStarredEventItems();
    if (this_present_starredEventItems || that_present_starredEventItems) {
      if (!(this_present_starredEventItems && that_present_starredEventItems))
        return false;
      if (!this.starredEventItems.equals(that.starredEventItems))
        return false;
    }

    boolean this_present_userTickets = true && this.isSetUserTickets();
    boolean that_present_userTickets = true && that.isSetUserTickets();
    if (this_present_userTickets || that_present_userTickets) {
      if (!(this_present_userTickets && that_present_userTickets))
        return false;
      if (!this.userTickets.equals(that.userTickets))
        return false;
    }

    boolean this_present_emailAddress = true && this.isSetEmailAddress();
    boolean that_present_emailAddress = true && that.isSetEmailAddress();
    if (this_present_emailAddress || that_present_emailAddress) {
      if (!(this_present_emailAddress && that_present_emailAddress))
        return false;
      if (!this.emailAddress.equals(that.emailAddress))
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

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_eventPoolId = true;
    list.add(present_eventPoolId);
    if (present_eventPoolId)
      list.add(eventPoolId);

    boolean present_starredEventItems = true && (isSetStarredEventItems());
    list.add(present_starredEventItems);
    if (present_starredEventItems)
      list.add(starredEventItems);

    boolean present_userTickets = true && (isSetUserTickets());
    list.add(present_userTickets);
    if (present_userTickets)
      list.add(userTickets);

    boolean present_emailAddress = true && (isSetEmailAddress());
    list.add(present_emailAddress);
    if (present_emailAddress)
      list.add(emailAddress);

    boolean present_lang = true && (isSetLang());
    list.add(present_lang);
    if (present_lang)
      list.add(lang);

    return list.hashCode();
  }

  @Override
  public int compareTo(SendEmailRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetEventPoolId()).compareTo(other.isSetEventPoolId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEventPoolId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.eventPoolId, other.eventPoolId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStarredEventItems()).compareTo(other.isSetStarredEventItems());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStarredEventItems()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.starredEventItems, other.starredEventItems);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserTickets()).compareTo(other.isSetUserTickets());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserTickets()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userTickets, other.userTickets);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEmailAddress()).compareTo(other.isSetEmailAddress());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEmailAddress()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.emailAddress, other.emailAddress);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLang()).compareTo(other.isSetLang());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLang()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.lang, other.lang);
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
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("SendEmailRequest(");
    boolean first = true;

    sb.append("eventPoolId:");
    sb.append(this.eventPoolId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("starredEventItems:");
    if (this.starredEventItems == null) {
      sb.append("null");
    } else {
      sb.append(this.starredEventItems);
    }
    first = false;
    if (isSetUserTickets()) {
      if (!first) sb.append(", ");
      sb.append("userTickets:");
      if (this.userTickets == null) {
        sb.append("null");
      } else {
        sb.append(this.userTickets);
      }
      first = false;
    }
    if (isSetEmailAddress()) {
      if (!first) sb.append(", ");
      sb.append("emailAddress:");
      if (this.emailAddress == null) {
        sb.append("null");
      } else {
        sb.append(this.emailAddress);
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
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'eventPoolId' because it's a primitive and you chose the non-beans generator.
    if (starredEventItems == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'starredEventItems' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
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
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SendEmailRequestStandardSchemeFactory implements SchemeFactory {
    public SendEmailRequestStandardScheme getScheme() {
      return new SendEmailRequestStandardScheme();
    }
  }

  private static class SendEmailRequestStandardScheme extends StandardScheme<SendEmailRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, SendEmailRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 4: // EVENT_POOL_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.eventPoolId = iprot.readI64();
              struct.setEventPoolIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 1: // STARRED_EVENT_ITEMS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list116 = iprot.readListBegin();
                struct.starredEventItems = new ArrayList<Long>(_list116.size);
                long _elem117;
                for (int _i118 = 0; _i118 < _list116.size; ++_i118)
                {
                  _elem117 = iprot.readI64();
                  struct.starredEventItems.add(_elem117);
                }
                iprot.readListEnd();
              }
              struct.setStarredEventItemsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // USER_TICKETS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list119 = iprot.readListBegin();
                struct.userTickets = new ArrayList<String>(_list119.size);
                String _elem120;
                for (int _i121 = 0; _i121 < _list119.size; ++_i121)
                {
                  _elem120 = iprot.readString();
                  struct.userTickets.add(_elem120);
                }
                iprot.readListEnd();
              }
              struct.setUserTicketsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // EMAIL_ADDRESS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.emailAddress = iprot.readString();
              struct.setEmailAddressIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // LANG
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.lang = iprot.readString();
              struct.setLangIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetEventPoolId()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'eventPoolId' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, SendEmailRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.starredEventItems != null) {
        oprot.writeFieldBegin(STARRED_EVENT_ITEMS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, struct.starredEventItems.size()));
          for (long _iter122 : struct.starredEventItems)
          {
            oprot.writeI64(_iter122);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.userTickets != null) {
        if (struct.isSetUserTickets()) {
          oprot.writeFieldBegin(USER_TICKETS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.userTickets.size()));
            for (String _iter123 : struct.userTickets)
            {
              oprot.writeString(_iter123);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.emailAddress != null) {
        if (struct.isSetEmailAddress()) {
          oprot.writeFieldBegin(EMAIL_ADDRESS_FIELD_DESC);
          oprot.writeString(struct.emailAddress);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldBegin(EVENT_POOL_ID_FIELD_DESC);
      oprot.writeI64(struct.eventPoolId);
      oprot.writeFieldEnd();
      if (struct.lang != null) {
        if (struct.isSetLang()) {
          oprot.writeFieldBegin(LANG_FIELD_DESC);
          oprot.writeString(struct.lang);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SendEmailRequestTupleSchemeFactory implements SchemeFactory {
    public SendEmailRequestTupleScheme getScheme() {
      return new SendEmailRequestTupleScheme();
    }
  }

  private static class SendEmailRequestTupleScheme extends TupleScheme<SendEmailRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, SendEmailRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.eventPoolId);
      {
        oprot.writeI32(struct.starredEventItems.size());
        for (long _iter124 : struct.starredEventItems)
        {
          oprot.writeI64(_iter124);
        }
      }
      BitSet optionals = new BitSet();
      if (struct.isSetUserTickets()) {
        optionals.set(0);
      }
      if (struct.isSetEmailAddress()) {
        optionals.set(1);
      }
      if (struct.isSetLang()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetUserTickets()) {
        {
          oprot.writeI32(struct.userTickets.size());
          for (String _iter125 : struct.userTickets)
          {
            oprot.writeString(_iter125);
          }
        }
      }
      if (struct.isSetEmailAddress()) {
        oprot.writeString(struct.emailAddress);
      }
      if (struct.isSetLang()) {
        oprot.writeString(struct.lang);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, SendEmailRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.eventPoolId = iprot.readI64();
      struct.setEventPoolIdIsSet(true);
      {
        org.apache.thrift.protocol.TList _list126 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, iprot.readI32());
        struct.starredEventItems = new ArrayList<Long>(_list126.size);
        long _elem127;
        for (int _i128 = 0; _i128 < _list126.size; ++_i128)
        {
          _elem127 = iprot.readI64();
          struct.starredEventItems.add(_elem127);
        }
      }
      struct.setStarredEventItemsIsSet(true);
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list129 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.userTickets = new ArrayList<String>(_list129.size);
          String _elem130;
          for (int _i131 = 0; _i131 < _list129.size; ++_i131)
          {
            _elem130 = iprot.readString();
            struct.userTickets.add(_elem130);
          }
        }
        struct.setUserTicketsIsSet(true);
      }
      if (incoming.get(1)) {
        struct.emailAddress = iprot.readString();
        struct.setEmailAddressIsSet(true);
      }
      if (incoming.get(2)) {
        struct.lang = iprot.readString();
        struct.setLangIsSet(true);
      }
    }
  }

}

