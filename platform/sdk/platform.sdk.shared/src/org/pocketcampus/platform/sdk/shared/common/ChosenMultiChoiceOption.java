/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.platform.sdk.shared.common;

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

public class ChosenMultiChoiceOption implements org.apache.thrift.TBase<ChosenMultiChoiceOption, ChosenMultiChoiceOption._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ChosenMultiChoiceOption");

  private static final org.apache.thrift.protocol.TField MULTI_CHOICE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("multiChoiceId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField CHOSEN_IDS_FIELD_DESC = new org.apache.thrift.protocol.TField("chosenIds", org.apache.thrift.protocol.TType.LIST, (short)2);

  public long multiChoiceId; // required
  public List<Long> chosenIds; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MULTI_CHOICE_ID((short)1, "multiChoiceId"),
    CHOSEN_IDS((short)2, "chosenIds");

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
        case 1: // MULTI_CHOICE_ID
          return MULTI_CHOICE_ID;
        case 2: // CHOSEN_IDS
          return CHOSEN_IDS;
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
  private static final int __MULTICHOICEID_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MULTI_CHOICE_ID, new org.apache.thrift.meta_data.FieldMetaData("multiChoiceId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Id")));
    tmpMap.put(_Fields.CHOSEN_IDS, new org.apache.thrift.meta_data.FieldMetaData("chosenIds", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64            , "Id"))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ChosenMultiChoiceOption.class, metaDataMap);
  }

  public ChosenMultiChoiceOption() {
  }

  public ChosenMultiChoiceOption(
    long multiChoiceId,
    List<Long> chosenIds)
  {
    this();
    this.multiChoiceId = multiChoiceId;
    setMultiChoiceIdIsSet(true);
    this.chosenIds = chosenIds;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ChosenMultiChoiceOption(ChosenMultiChoiceOption other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.multiChoiceId = other.multiChoiceId;
    if (other.isSetChosenIds()) {
      List<Long> __this__chosenIds = new ArrayList<Long>();
      for (Long other_element : other.chosenIds) {
        __this__chosenIds.add(other_element);
      }
      this.chosenIds = __this__chosenIds;
    }
  }

  public ChosenMultiChoiceOption deepCopy() {
    return new ChosenMultiChoiceOption(this);
  }

  @Override
  public void clear() {
    setMultiChoiceIdIsSet(false);
    this.multiChoiceId = 0;
    this.chosenIds = null;
  }

  public long getMultiChoiceId() {
    return this.multiChoiceId;
  }

  public ChosenMultiChoiceOption setMultiChoiceId(long multiChoiceId) {
    this.multiChoiceId = multiChoiceId;
    setMultiChoiceIdIsSet(true);
    return this;
  }

  public void unsetMultiChoiceId() {
    __isset_bit_vector.clear(__MULTICHOICEID_ISSET_ID);
  }

  /** Returns true if field multiChoiceId is set (has been assigned a value) and false otherwise */
  public boolean isSetMultiChoiceId() {
    return __isset_bit_vector.get(__MULTICHOICEID_ISSET_ID);
  }

  public void setMultiChoiceIdIsSet(boolean value) {
    __isset_bit_vector.set(__MULTICHOICEID_ISSET_ID, value);
  }

  public int getChosenIdsSize() {
    return (this.chosenIds == null) ? 0 : this.chosenIds.size();
  }

  public java.util.Iterator<Long> getChosenIdsIterator() {
    return (this.chosenIds == null) ? null : this.chosenIds.iterator();
  }

  public void addToChosenIds(long elem) {
    if (this.chosenIds == null) {
      this.chosenIds = new ArrayList<Long>();
    }
    this.chosenIds.add(elem);
  }

  public List<Long> getChosenIds() {
    return this.chosenIds;
  }

  public ChosenMultiChoiceOption setChosenIds(List<Long> chosenIds) {
    this.chosenIds = chosenIds;
    return this;
  }

  public void unsetChosenIds() {
    this.chosenIds = null;
  }

  /** Returns true if field chosenIds is set (has been assigned a value) and false otherwise */
  public boolean isSetChosenIds() {
    return this.chosenIds != null;
  }

  public void setChosenIdsIsSet(boolean value) {
    if (!value) {
      this.chosenIds = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MULTI_CHOICE_ID:
      if (value == null) {
        unsetMultiChoiceId();
      } else {
        setMultiChoiceId((Long)value);
      }
      break;

    case CHOSEN_IDS:
      if (value == null) {
        unsetChosenIds();
      } else {
        setChosenIds((List<Long>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MULTI_CHOICE_ID:
      return Long.valueOf(getMultiChoiceId());

    case CHOSEN_IDS:
      return getChosenIds();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MULTI_CHOICE_ID:
      return isSetMultiChoiceId();
    case CHOSEN_IDS:
      return isSetChosenIds();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ChosenMultiChoiceOption)
      return this.equals((ChosenMultiChoiceOption)that);
    return false;
  }

  public boolean equals(ChosenMultiChoiceOption that) {
    if (that == null)
      return false;

    boolean this_present_multiChoiceId = true;
    boolean that_present_multiChoiceId = true;
    if (this_present_multiChoiceId || that_present_multiChoiceId) {
      if (!(this_present_multiChoiceId && that_present_multiChoiceId))
        return false;
      if (this.multiChoiceId != that.multiChoiceId)
        return false;
    }

    boolean this_present_chosenIds = true && this.isSetChosenIds();
    boolean that_present_chosenIds = true && that.isSetChosenIds();
    if (this_present_chosenIds || that_present_chosenIds) {
      if (!(this_present_chosenIds && that_present_chosenIds))
        return false;
      if (!this.chosenIds.equals(that.chosenIds))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_multiChoiceId = true;
    builder.append(present_multiChoiceId);
    if (present_multiChoiceId)
      builder.append(multiChoiceId);

    boolean present_chosenIds = true && (isSetChosenIds());
    builder.append(present_chosenIds);
    if (present_chosenIds)
      builder.append(chosenIds);

    return builder.toHashCode();
  }

  public int compareTo(ChosenMultiChoiceOption other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    ChosenMultiChoiceOption typedOther = (ChosenMultiChoiceOption)other;

    lastComparison = Boolean.valueOf(isSetMultiChoiceId()).compareTo(typedOther.isSetMultiChoiceId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMultiChoiceId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.multiChoiceId, typedOther.multiChoiceId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetChosenIds()).compareTo(typedOther.isSetChosenIds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetChosenIds()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.chosenIds, typedOther.chosenIds);
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
        case 1: // MULTI_CHOICE_ID
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.multiChoiceId = iprot.readI64();
            setMultiChoiceIdIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // CHOSEN_IDS
          if (field.type == org.apache.thrift.protocol.TType.LIST) {
            {
              org.apache.thrift.protocol.TList _list12 = iprot.readListBegin();
              this.chosenIds = new ArrayList<Long>(_list12.size);
              for (int _i13 = 0; _i13 < _list12.size; ++_i13)
              {
                long _elem14; // required
                _elem14 = iprot.readI64();
                this.chosenIds.add(_elem14);
              }
              iprot.readListEnd();
            }
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
    if (!isSetMultiChoiceId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'multiChoiceId' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(MULTI_CHOICE_ID_FIELD_DESC);
    oprot.writeI64(this.multiChoiceId);
    oprot.writeFieldEnd();
    if (this.chosenIds != null) {
      oprot.writeFieldBegin(CHOSEN_IDS_FIELD_DESC);
      {
        oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, this.chosenIds.size()));
        for (long _iter15 : this.chosenIds)
        {
          oprot.writeI64(_iter15);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ChosenMultiChoiceOption(");
    boolean first = true;

    sb.append("multiChoiceId:");
    sb.append(this.multiChoiceId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("chosenIds:");
    if (this.chosenIds == null) {
      sb.append("null");
    } else {
      sb.append(this.chosenIds);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'multiChoiceId' because it's a primitive and you chose the non-beans generator.
    if (chosenIds == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'chosenIds' was not present! Struct: " + toString());
    }
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

