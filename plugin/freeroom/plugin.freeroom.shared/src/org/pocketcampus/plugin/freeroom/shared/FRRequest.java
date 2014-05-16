/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.freeroom.shared;

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

public class FRRequest implements org.apache.thrift.TBase<FRRequest, FRRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FRRequest");

  private static final org.apache.thrift.protocol.TField PERIOD_FIELD_DESC = new org.apache.thrift.protocol.TField("period", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField ONLY_FREE_ROOMS_FIELD_DESC = new org.apache.thrift.protocol.TField("onlyFreeRooms", org.apache.thrift.protocol.TType.BOOL, (short)2);
  private static final org.apache.thrift.protocol.TField UID_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("uidList", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField USER_GROUP_FIELD_DESC = new org.apache.thrift.protocol.TField("userGroup", org.apache.thrift.protocol.TType.I32, (short)4);

  private FRPeriod period; // required
  private boolean onlyFreeRooms; // required
  private List<String> uidList; // required
  private int userGroup; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PERIOD((short)1, "period"),
    ONLY_FREE_ROOMS((short)2, "onlyFreeRooms"),
    UID_LIST((short)3, "uidList"),
    USER_GROUP((short)4, "userGroup");

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
        case 1: // PERIOD
          return PERIOD;
        case 2: // ONLY_FREE_ROOMS
          return ONLY_FREE_ROOMS;
        case 3: // UID_LIST
          return UID_LIST;
        case 4: // USER_GROUP
          return USER_GROUP;
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
  private static final int __ONLYFREEROOMS_ISSET_ID = 0;
  private static final int __USERGROUP_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.PERIOD, new org.apache.thrift.meta_data.FieldMetaData("period", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, FRPeriod.class)));
    tmpMap.put(_Fields.ONLY_FREE_ROOMS, new org.apache.thrift.meta_data.FieldMetaData("onlyFreeRooms", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.UID_LIST, new org.apache.thrift.meta_data.FieldMetaData("uidList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.USER_GROUP, new org.apache.thrift.meta_data.FieldMetaData("userGroup", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FRRequest.class, metaDataMap);
  }

  public FRRequest() {
  }

  public FRRequest(
    FRPeriod period,
    boolean onlyFreeRooms,
    List<String> uidList,
    int userGroup)
  {
    this();
    this.period = period;
    this.onlyFreeRooms = onlyFreeRooms;
    setOnlyFreeRoomsIsSet(true);
    this.uidList = uidList;
    this.userGroup = userGroup;
    setUserGroupIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FRRequest(FRRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetPeriod()) {
      this.period = new FRPeriod(other.period);
    }
    this.onlyFreeRooms = other.onlyFreeRooms;
    if (other.isSetUidList()) {
      List<String> __this__uidList = new ArrayList<String>();
      for (String other_element : other.uidList) {
        __this__uidList.add(other_element);
      }
      this.uidList = __this__uidList;
    }
    this.userGroup = other.userGroup;
  }

  public FRRequest deepCopy() {
    return new FRRequest(this);
  }

  @Override
  public void clear() {
    this.period = null;
    setOnlyFreeRoomsIsSet(false);
    this.onlyFreeRooms = false;
    this.uidList = null;
    setUserGroupIsSet(false);
    this.userGroup = 0;
  }

  public FRPeriod getPeriod() {
    return this.period;
  }

  public FRRequest setPeriod(FRPeriod period) {
    this.period = period;
    return this;
  }

  public void unsetPeriod() {
    this.period = null;
  }

  /** Returns true if field period is set (has been assigned a value) and false otherwise */
  public boolean isSetPeriod() {
    return this.period != null;
  }

  public void setPeriodIsSet(boolean value) {
    if (!value) {
      this.period = null;
    }
  }

  public boolean isOnlyFreeRooms() {
    return this.onlyFreeRooms;
  }

  public FRRequest setOnlyFreeRooms(boolean onlyFreeRooms) {
    this.onlyFreeRooms = onlyFreeRooms;
    setOnlyFreeRoomsIsSet(true);
    return this;
  }

  public void unsetOnlyFreeRooms() {
    __isset_bit_vector.clear(__ONLYFREEROOMS_ISSET_ID);
  }

  /** Returns true if field onlyFreeRooms is set (has been assigned a value) and false otherwise */
  public boolean isSetOnlyFreeRooms() {
    return __isset_bit_vector.get(__ONLYFREEROOMS_ISSET_ID);
  }

  public void setOnlyFreeRoomsIsSet(boolean value) {
    __isset_bit_vector.set(__ONLYFREEROOMS_ISSET_ID, value);
  }

  public int getUidListSize() {
    return (this.uidList == null) ? 0 : this.uidList.size();
  }

  public java.util.Iterator<String> getUidListIterator() {
    return (this.uidList == null) ? null : this.uidList.iterator();
  }

  public void addToUidList(String elem) {
    if (this.uidList == null) {
      this.uidList = new ArrayList<String>();
    }
    this.uidList.add(elem);
  }

  public List<String> getUidList() {
    return this.uidList;
  }

  public FRRequest setUidList(List<String> uidList) {
    this.uidList = uidList;
    return this;
  }

  public void unsetUidList() {
    this.uidList = null;
  }

  /** Returns true if field uidList is set (has been assigned a value) and false otherwise */
  public boolean isSetUidList() {
    return this.uidList != null;
  }

  public void setUidListIsSet(boolean value) {
    if (!value) {
      this.uidList = null;
    }
  }

  public int getUserGroup() {
    return this.userGroup;
  }

  public FRRequest setUserGroup(int userGroup) {
    this.userGroup = userGroup;
    setUserGroupIsSet(true);
    return this;
  }

  public void unsetUserGroup() {
    __isset_bit_vector.clear(__USERGROUP_ISSET_ID);
  }

  /** Returns true if field userGroup is set (has been assigned a value) and false otherwise */
  public boolean isSetUserGroup() {
    return __isset_bit_vector.get(__USERGROUP_ISSET_ID);
  }

  public void setUserGroupIsSet(boolean value) {
    __isset_bit_vector.set(__USERGROUP_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case PERIOD:
      if (value == null) {
        unsetPeriod();
      } else {
        setPeriod((FRPeriod)value);
      }
      break;

    case ONLY_FREE_ROOMS:
      if (value == null) {
        unsetOnlyFreeRooms();
      } else {
        setOnlyFreeRooms((Boolean)value);
      }
      break;

    case UID_LIST:
      if (value == null) {
        unsetUidList();
      } else {
        setUidList((List<String>)value);
      }
      break;

    case USER_GROUP:
      if (value == null) {
        unsetUserGroup();
      } else {
        setUserGroup((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case PERIOD:
      return getPeriod();

    case ONLY_FREE_ROOMS:
      return Boolean.valueOf(isOnlyFreeRooms());

    case UID_LIST:
      return getUidList();

    case USER_GROUP:
      return Integer.valueOf(getUserGroup());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case PERIOD:
      return isSetPeriod();
    case ONLY_FREE_ROOMS:
      return isSetOnlyFreeRooms();
    case UID_LIST:
      return isSetUidList();
    case USER_GROUP:
      return isSetUserGroup();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof FRRequest)
      return this.equals((FRRequest)that);
    return false;
  }

  public boolean equals(FRRequest that) {
    if (that == null)
      return false;

    boolean this_present_period = true && this.isSetPeriod();
    boolean that_present_period = true && that.isSetPeriod();
    if (this_present_period || that_present_period) {
      if (!(this_present_period && that_present_period))
        return false;
      if (!this.period.equals(that.period))
        return false;
    }

    boolean this_present_onlyFreeRooms = true;
    boolean that_present_onlyFreeRooms = true;
    if (this_present_onlyFreeRooms || that_present_onlyFreeRooms) {
      if (!(this_present_onlyFreeRooms && that_present_onlyFreeRooms))
        return false;
      if (this.onlyFreeRooms != that.onlyFreeRooms)
        return false;
    }

    boolean this_present_uidList = true && this.isSetUidList();
    boolean that_present_uidList = true && that.isSetUidList();
    if (this_present_uidList || that_present_uidList) {
      if (!(this_present_uidList && that_present_uidList))
        return false;
      if (!this.uidList.equals(that.uidList))
        return false;
    }

    boolean this_present_userGroup = true;
    boolean that_present_userGroup = true;
    if (this_present_userGroup || that_present_userGroup) {
      if (!(this_present_userGroup && that_present_userGroup))
        return false;
      if (this.userGroup != that.userGroup)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_period = true && (isSetPeriod());
    builder.append(present_period);
    if (present_period)
      builder.append(period);

    boolean present_onlyFreeRooms = true;
    builder.append(present_onlyFreeRooms);
    if (present_onlyFreeRooms)
      builder.append(onlyFreeRooms);

    boolean present_uidList = true && (isSetUidList());
    builder.append(present_uidList);
    if (present_uidList)
      builder.append(uidList);

    boolean present_userGroup = true;
    builder.append(present_userGroup);
    if (present_userGroup)
      builder.append(userGroup);

    return builder.toHashCode();
  }

  public int compareTo(FRRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    FRRequest typedOther = (FRRequest)other;

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
    lastComparison = Boolean.valueOf(isSetOnlyFreeRooms()).compareTo(typedOther.isSetOnlyFreeRooms());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOnlyFreeRooms()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.onlyFreeRooms, typedOther.onlyFreeRooms);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUidList()).compareTo(typedOther.isSetUidList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUidList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.uidList, typedOther.uidList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserGroup()).compareTo(typedOther.isSetUserGroup());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserGroup()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userGroup, typedOther.userGroup);
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
        case 1: // PERIOD
          if (field.type == org.apache.thrift.protocol.TType.STRUCT) {
            this.period = new FRPeriod();
            this.period.read(iprot);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // ONLY_FREE_ROOMS
          if (field.type == org.apache.thrift.protocol.TType.BOOL) {
            this.onlyFreeRooms = iprot.readBool();
            setOnlyFreeRoomsIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // UID_LIST
          if (field.type == org.apache.thrift.protocol.TType.LIST) {
            {
              org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
              this.uidList = new ArrayList<String>(_list8.size);
              for (int _i9 = 0; _i9 < _list8.size; ++_i9)
              {
                String _elem10; // required
                _elem10 = iprot.readString();
                this.uidList.add(_elem10);
              }
              iprot.readListEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // USER_GROUP
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.userGroup = iprot.readI32();
            setUserGroupIsSet(true);
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
    if (!isSetOnlyFreeRooms()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'onlyFreeRooms' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetUserGroup()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'userGroup' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.period != null) {
      oprot.writeFieldBegin(PERIOD_FIELD_DESC);
      this.period.write(oprot);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(ONLY_FREE_ROOMS_FIELD_DESC);
    oprot.writeBool(this.onlyFreeRooms);
    oprot.writeFieldEnd();
    if (this.uidList != null) {
      oprot.writeFieldBegin(UID_LIST_FIELD_DESC);
      {
        oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, this.uidList.size()));
        for (String _iter11 : this.uidList)
        {
          oprot.writeString(_iter11);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(USER_GROUP_FIELD_DESC);
    oprot.writeI32(this.userGroup);
    oprot.writeFieldEnd();
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("FRRequest(");
    boolean first = true;

    sb.append("period:");
    if (this.period == null) {
      sb.append("null");
    } else {
      sb.append(this.period);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("onlyFreeRooms:");
    sb.append(this.onlyFreeRooms);
    first = false;
    if (!first) sb.append(", ");
    sb.append("uidList:");
    if (this.uidList == null) {
      sb.append("null");
    } else {
      sb.append(this.uidList);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("userGroup:");
    sb.append(this.userGroup);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (period == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'period' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'onlyFreeRooms' because it's a primitive and you chose the non-beans generator.
    if (uidList == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'uidList' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'userGroup' because it's a primitive and you chose the non-beans generator.
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

