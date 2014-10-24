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

public class FRAutoCompleteRequest implements org.apache.thrift.TBase<FRAutoCompleteRequest, FRAutoCompleteRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("FRAutoCompleteRequest");

  private static final org.apache.thrift.protocol.TField CONSTRAINT_FIELD_DESC = new org.apache.thrift.protocol.TField("constraint", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField FORBIDDEN_ROOMS_UID_FIELD_DESC = new org.apache.thrift.protocol.TField("forbiddenRoomsUID", org.apache.thrift.protocol.TType.SET, (short)2);
  private static final org.apache.thrift.protocol.TField USER_GROUP_FIELD_DESC = new org.apache.thrift.protocol.TField("userGroup", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField EXACT_STRING_FIELD_DESC = new org.apache.thrift.protocol.TField("exactString", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField USER_LANGUAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("userLanguage", org.apache.thrift.protocol.TType.I32, (short)5);

  private String constraint; // required
  private Set<String> forbiddenRoomsUID; // required
  private int userGroup; // required
  private boolean exactString; // required
  private FRLanguage userLanguage; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    CONSTRAINT((short)1, "constraint"),
    FORBIDDEN_ROOMS_UID((short)2, "forbiddenRoomsUID"),
    USER_GROUP((short)3, "userGroup"),
    EXACT_STRING((short)4, "exactString"),
    /**
     * 
     * @see FRLanguage
     */
    USER_LANGUAGE((short)5, "userLanguage");

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
        case 1: // CONSTRAINT
          return CONSTRAINT;
        case 2: // FORBIDDEN_ROOMS_UID
          return FORBIDDEN_ROOMS_UID;
        case 3: // USER_GROUP
          return USER_GROUP;
        case 4: // EXACT_STRING
          return EXACT_STRING;
        case 5: // USER_LANGUAGE
          return USER_LANGUAGE;
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
  private static final int __USERGROUP_ISSET_ID = 0;
  private static final int __EXACTSTRING_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CONSTRAINT, new org.apache.thrift.meta_data.FieldMetaData("constraint", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.FORBIDDEN_ROOMS_UID, new org.apache.thrift.meta_data.FieldMetaData("forbiddenRoomsUID", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.USER_GROUP, new org.apache.thrift.meta_data.FieldMetaData("userGroup", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.EXACT_STRING, new org.apache.thrift.meta_data.FieldMetaData("exactString", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.USER_LANGUAGE, new org.apache.thrift.meta_data.FieldMetaData("userLanguage", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, FRLanguage.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(FRAutoCompleteRequest.class, metaDataMap);
  }

  public FRAutoCompleteRequest() {
  }

  public FRAutoCompleteRequest(
    String constraint,
    int userGroup)
  {
    this();
    this.constraint = constraint;
    this.userGroup = userGroup;
    setUserGroupIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public FRAutoCompleteRequest(FRAutoCompleteRequest other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetConstraint()) {
      this.constraint = other.constraint;
    }
    if (other.isSetForbiddenRoomsUID()) {
      Set<String> __this__forbiddenRoomsUID = new HashSet<String>();
      for (String other_element : other.forbiddenRoomsUID) {
        __this__forbiddenRoomsUID.add(other_element);
      }
      this.forbiddenRoomsUID = __this__forbiddenRoomsUID;
    }
    this.userGroup = other.userGroup;
    this.exactString = other.exactString;
    if (other.isSetUserLanguage()) {
      this.userLanguage = other.userLanguage;
    }
  }

  public FRAutoCompleteRequest deepCopy() {
    return new FRAutoCompleteRequest(this);
  }

  @Override
  public void clear() {
    this.constraint = null;
    this.forbiddenRoomsUID = null;
    setUserGroupIsSet(false);
    this.userGroup = 0;
    setExactStringIsSet(false);
    this.exactString = false;
    this.userLanguage = null;
  }

  public String getConstraint() {
    return this.constraint;
  }

  public FRAutoCompleteRequest setConstraint(String constraint) {
    this.constraint = constraint;
    return this;
  }

  public void unsetConstraint() {
    this.constraint = null;
  }

  /** Returns true if field constraint is set (has been assigned a value) and false otherwise */
  public boolean isSetConstraint() {
    return this.constraint != null;
  }

  public void setConstraintIsSet(boolean value) {
    if (!value) {
      this.constraint = null;
    }
  }

  public int getForbiddenRoomsUIDSize() {
    return (this.forbiddenRoomsUID == null) ? 0 : this.forbiddenRoomsUID.size();
  }

  public java.util.Iterator<String> getForbiddenRoomsUIDIterator() {
    return (this.forbiddenRoomsUID == null) ? null : this.forbiddenRoomsUID.iterator();
  }

  public void addToForbiddenRoomsUID(String elem) {
    if (this.forbiddenRoomsUID == null) {
      this.forbiddenRoomsUID = new HashSet<String>();
    }
    this.forbiddenRoomsUID.add(elem);
  }

  public Set<String> getForbiddenRoomsUID() {
    return this.forbiddenRoomsUID;
  }

  public FRAutoCompleteRequest setForbiddenRoomsUID(Set<String> forbiddenRoomsUID) {
    this.forbiddenRoomsUID = forbiddenRoomsUID;
    return this;
  }

  public void unsetForbiddenRoomsUID() {
    this.forbiddenRoomsUID = null;
  }

  /** Returns true if field forbiddenRoomsUID is set (has been assigned a value) and false otherwise */
  public boolean isSetForbiddenRoomsUID() {
    return this.forbiddenRoomsUID != null;
  }

  public void setForbiddenRoomsUIDIsSet(boolean value) {
    if (!value) {
      this.forbiddenRoomsUID = null;
    }
  }

  public int getUserGroup() {
    return this.userGroup;
  }

  public FRAutoCompleteRequest setUserGroup(int userGroup) {
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

  public boolean isExactString() {
    return this.exactString;
  }

  public FRAutoCompleteRequest setExactString(boolean exactString) {
    this.exactString = exactString;
    setExactStringIsSet(true);
    return this;
  }

  public void unsetExactString() {
    __isset_bit_vector.clear(__EXACTSTRING_ISSET_ID);
  }

  /** Returns true if field exactString is set (has been assigned a value) and false otherwise */
  public boolean isSetExactString() {
    return __isset_bit_vector.get(__EXACTSTRING_ISSET_ID);
  }

  public void setExactStringIsSet(boolean value) {
    __isset_bit_vector.set(__EXACTSTRING_ISSET_ID, value);
  }

  /**
   * 
   * @see FRLanguage
   */
  public FRLanguage getUserLanguage() {
    return this.userLanguage;
  }

  /**
   * 
   * @see FRLanguage
   */
  public FRAutoCompleteRequest setUserLanguage(FRLanguage userLanguage) {
    this.userLanguage = userLanguage;
    return this;
  }

  public void unsetUserLanguage() {
    this.userLanguage = null;
  }

  /** Returns true if field userLanguage is set (has been assigned a value) and false otherwise */
  public boolean isSetUserLanguage() {
    return this.userLanguage != null;
  }

  public void setUserLanguageIsSet(boolean value) {
    if (!value) {
      this.userLanguage = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case CONSTRAINT:
      if (value == null) {
        unsetConstraint();
      } else {
        setConstraint((String)value);
      }
      break;

    case FORBIDDEN_ROOMS_UID:
      if (value == null) {
        unsetForbiddenRoomsUID();
      } else {
        setForbiddenRoomsUID((Set<String>)value);
      }
      break;

    case USER_GROUP:
      if (value == null) {
        unsetUserGroup();
      } else {
        setUserGroup((Integer)value);
      }
      break;

    case EXACT_STRING:
      if (value == null) {
        unsetExactString();
      } else {
        setExactString((Boolean)value);
      }
      break;

    case USER_LANGUAGE:
      if (value == null) {
        unsetUserLanguage();
      } else {
        setUserLanguage((FRLanguage)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case CONSTRAINT:
      return getConstraint();

    case FORBIDDEN_ROOMS_UID:
      return getForbiddenRoomsUID();

    case USER_GROUP:
      return Integer.valueOf(getUserGroup());

    case EXACT_STRING:
      return Boolean.valueOf(isExactString());

    case USER_LANGUAGE:
      return getUserLanguage();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case CONSTRAINT:
      return isSetConstraint();
    case FORBIDDEN_ROOMS_UID:
      return isSetForbiddenRoomsUID();
    case USER_GROUP:
      return isSetUserGroup();
    case EXACT_STRING:
      return isSetExactString();
    case USER_LANGUAGE:
      return isSetUserLanguage();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof FRAutoCompleteRequest)
      return this.equals((FRAutoCompleteRequest)that);
    return false;
  }

  public boolean equals(FRAutoCompleteRequest that) {
    if (that == null)
      return false;

    boolean this_present_constraint = true && this.isSetConstraint();
    boolean that_present_constraint = true && that.isSetConstraint();
    if (this_present_constraint || that_present_constraint) {
      if (!(this_present_constraint && that_present_constraint))
        return false;
      if (!this.constraint.equals(that.constraint))
        return false;
    }

    boolean this_present_forbiddenRoomsUID = true && this.isSetForbiddenRoomsUID();
    boolean that_present_forbiddenRoomsUID = true && that.isSetForbiddenRoomsUID();
    if (this_present_forbiddenRoomsUID || that_present_forbiddenRoomsUID) {
      if (!(this_present_forbiddenRoomsUID && that_present_forbiddenRoomsUID))
        return false;
      if (!this.forbiddenRoomsUID.equals(that.forbiddenRoomsUID))
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

    boolean this_present_exactString = true && this.isSetExactString();
    boolean that_present_exactString = true && that.isSetExactString();
    if (this_present_exactString || that_present_exactString) {
      if (!(this_present_exactString && that_present_exactString))
        return false;
      if (this.exactString != that.exactString)
        return false;
    }

    boolean this_present_userLanguage = true && this.isSetUserLanguage();
    boolean that_present_userLanguage = true && that.isSetUserLanguage();
    if (this_present_userLanguage || that_present_userLanguage) {
      if (!(this_present_userLanguage && that_present_userLanguage))
        return false;
      if (!this.userLanguage.equals(that.userLanguage))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_constraint = true && (isSetConstraint());
    builder.append(present_constraint);
    if (present_constraint)
      builder.append(constraint);

    boolean present_forbiddenRoomsUID = true && (isSetForbiddenRoomsUID());
    builder.append(present_forbiddenRoomsUID);
    if (present_forbiddenRoomsUID)
      builder.append(forbiddenRoomsUID);

    boolean present_userGroup = true;
    builder.append(present_userGroup);
    if (present_userGroup)
      builder.append(userGroup);

    boolean present_exactString = true && (isSetExactString());
    builder.append(present_exactString);
    if (present_exactString)
      builder.append(exactString);

    boolean present_userLanguage = true && (isSetUserLanguage());
    builder.append(present_userLanguage);
    if (present_userLanguage)
      builder.append(userLanguage.getValue());

    return builder.toHashCode();
  }

  public int compareTo(FRAutoCompleteRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    FRAutoCompleteRequest typedOther = (FRAutoCompleteRequest)other;

    lastComparison = Boolean.valueOf(isSetConstraint()).compareTo(typedOther.isSetConstraint());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetConstraint()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.constraint, typedOther.constraint);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetForbiddenRoomsUID()).compareTo(typedOther.isSetForbiddenRoomsUID());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetForbiddenRoomsUID()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.forbiddenRoomsUID, typedOther.forbiddenRoomsUID);
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
    lastComparison = Boolean.valueOf(isSetExactString()).compareTo(typedOther.isSetExactString());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetExactString()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.exactString, typedOther.exactString);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserLanguage()).compareTo(typedOther.isSetUserLanguage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserLanguage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userLanguage, typedOther.userLanguage);
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
        case 1: // CONSTRAINT
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.constraint = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // FORBIDDEN_ROOMS_UID
          if (field.type == org.apache.thrift.protocol.TType.SET) {
            {
              org.apache.thrift.protocol.TSet _set17 = iprot.readSetBegin();
              this.forbiddenRoomsUID = new HashSet<String>(2*_set17.size);
              for (int _i18 = 0; _i18 < _set17.size; ++_i18)
              {
                String _elem19; // required
                _elem19 = iprot.readString();
                this.forbiddenRoomsUID.add(_elem19);
              }
              iprot.readSetEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // USER_GROUP
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.userGroup = iprot.readI32();
            setUserGroupIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // EXACT_STRING
          if (field.type == org.apache.thrift.protocol.TType.BOOL) {
            this.exactString = iprot.readBool();
            setExactStringIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // USER_LANGUAGE
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.userLanguage = FRLanguage.findByValue(iprot.readI32());
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
    if (!isSetUserGroup()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'userGroup' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.constraint != null) {
      oprot.writeFieldBegin(CONSTRAINT_FIELD_DESC);
      oprot.writeString(this.constraint);
      oprot.writeFieldEnd();
    }
    if (this.forbiddenRoomsUID != null) {
      if (isSetForbiddenRoomsUID()) {
        oprot.writeFieldBegin(FORBIDDEN_ROOMS_UID_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRING, this.forbiddenRoomsUID.size()));
          for (String _iter20 : this.forbiddenRoomsUID)
          {
            oprot.writeString(_iter20);
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldBegin(USER_GROUP_FIELD_DESC);
    oprot.writeI32(this.userGroup);
    oprot.writeFieldEnd();
    if (isSetExactString()) {
      oprot.writeFieldBegin(EXACT_STRING_FIELD_DESC);
      oprot.writeBool(this.exactString);
      oprot.writeFieldEnd();
    }
    if (this.userLanguage != null) {
      if (isSetUserLanguage()) {
        oprot.writeFieldBegin(USER_LANGUAGE_FIELD_DESC);
        oprot.writeI32(this.userLanguage.getValue());
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("FRAutoCompleteRequest(");
    boolean first = true;

    sb.append("constraint:");
    if (this.constraint == null) {
      sb.append("null");
    } else {
      sb.append(this.constraint);
    }
    first = false;
    if (isSetForbiddenRoomsUID()) {
      if (!first) sb.append(", ");
      sb.append("forbiddenRoomsUID:");
      if (this.forbiddenRoomsUID == null) {
        sb.append("null");
      } else {
        sb.append(this.forbiddenRoomsUID);
      }
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("userGroup:");
    sb.append(this.userGroup);
    first = false;
    if (isSetExactString()) {
      if (!first) sb.append(", ");
      sb.append("exactString:");
      sb.append(this.exactString);
      first = false;
    }
    if (isSetUserLanguage()) {
      if (!first) sb.append(", ");
      sb.append("userLanguage:");
      if (this.userLanguage == null) {
        sb.append("null");
      } else {
        sb.append(this.userLanguage);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (constraint == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'constraint' was not present! Struct: " + toString());
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

