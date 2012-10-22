/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.myedu.shared;

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

public class MyEduRequest implements org.apache.thrift.TBase<MyEduRequest, MyEduRequest._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MyEduRequest");

  private static final org.apache.thrift.protocol.TField I_MY_EDU_SESSION_FIELD_DESC = new org.apache.thrift.protocol.TField("iMyEduSession", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField I_LANGUAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("iLanguage", org.apache.thrift.protocol.TType.STRING, (short)2);

  public MyEduSession iMyEduSession; // required
  public String iLanguage; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    I_MY_EDU_SESSION((short)1, "iMyEduSession"),
    I_LANGUAGE((short)2, "iLanguage");

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
        case 1: // I_MY_EDU_SESSION
          return I_MY_EDU_SESSION;
        case 2: // I_LANGUAGE
          return I_LANGUAGE;
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

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.I_MY_EDU_SESSION, new org.apache.thrift.meta_data.FieldMetaData("iMyEduSession", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, MyEduSession.class)));
    tmpMap.put(_Fields.I_LANGUAGE, new org.apache.thrift.meta_data.FieldMetaData("iLanguage", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MyEduRequest.class, metaDataMap);
  }

  public MyEduRequest() {
  }

  public MyEduRequest(
    MyEduSession iMyEduSession)
  {
    this();
    this.iMyEduSession = iMyEduSession;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MyEduRequest(MyEduRequest other) {
    if (other.isSetIMyEduSession()) {
      this.iMyEduSession = new MyEduSession(other.iMyEduSession);
    }
    if (other.isSetILanguage()) {
      this.iLanguage = other.iLanguage;
    }
  }

  public MyEduRequest deepCopy() {
    return new MyEduRequest(this);
  }

  @Override
  public void clear() {
    this.iMyEduSession = null;
    this.iLanguage = null;
  }

  public MyEduSession getIMyEduSession() {
    return this.iMyEduSession;
  }

  public MyEduRequest setIMyEduSession(MyEduSession iMyEduSession) {
    this.iMyEduSession = iMyEduSession;
    return this;
  }

  public void unsetIMyEduSession() {
    this.iMyEduSession = null;
  }

  /** Returns true if field iMyEduSession is set (has been assigned a value) and false otherwise */
  public boolean isSetIMyEduSession() {
    return this.iMyEduSession != null;
  }

  public void setIMyEduSessionIsSet(boolean value) {
    if (!value) {
      this.iMyEduSession = null;
    }
  }

  public String getILanguage() {
    return this.iLanguage;
  }

  public MyEduRequest setILanguage(String iLanguage) {
    this.iLanguage = iLanguage;
    return this;
  }

  public void unsetILanguage() {
    this.iLanguage = null;
  }

  /** Returns true if field iLanguage is set (has been assigned a value) and false otherwise */
  public boolean isSetILanguage() {
    return this.iLanguage != null;
  }

  public void setILanguageIsSet(boolean value) {
    if (!value) {
      this.iLanguage = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case I_MY_EDU_SESSION:
      if (value == null) {
        unsetIMyEduSession();
      } else {
        setIMyEduSession((MyEduSession)value);
      }
      break;

    case I_LANGUAGE:
      if (value == null) {
        unsetILanguage();
      } else {
        setILanguage((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case I_MY_EDU_SESSION:
      return getIMyEduSession();

    case I_LANGUAGE:
      return getILanguage();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case I_MY_EDU_SESSION:
      return isSetIMyEduSession();
    case I_LANGUAGE:
      return isSetILanguage();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MyEduRequest)
      return this.equals((MyEduRequest)that);
    return false;
  }

  public boolean equals(MyEduRequest that) {
    if (that == null)
      return false;

    boolean this_present_iMyEduSession = true && this.isSetIMyEduSession();
    boolean that_present_iMyEduSession = true && that.isSetIMyEduSession();
    if (this_present_iMyEduSession || that_present_iMyEduSession) {
      if (!(this_present_iMyEduSession && that_present_iMyEduSession))
        return false;
      if (!this.iMyEduSession.equals(that.iMyEduSession))
        return false;
    }

    boolean this_present_iLanguage = true && this.isSetILanguage();
    boolean that_present_iLanguage = true && that.isSetILanguage();
    if (this_present_iLanguage || that_present_iLanguage) {
      if (!(this_present_iLanguage && that_present_iLanguage))
        return false;
      if (!this.iLanguage.equals(that.iLanguage))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_iMyEduSession = true && (isSetIMyEduSession());
    builder.append(present_iMyEduSession);
    if (present_iMyEduSession)
      builder.append(iMyEduSession);

    boolean present_iLanguage = true && (isSetILanguage());
    builder.append(present_iLanguage);
    if (present_iLanguage)
      builder.append(iLanguage);

    return builder.toHashCode();
  }

  public int compareTo(MyEduRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    MyEduRequest typedOther = (MyEduRequest)other;

    lastComparison = Boolean.valueOf(isSetIMyEduSession()).compareTo(typedOther.isSetIMyEduSession());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIMyEduSession()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iMyEduSession, typedOther.iMyEduSession);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetILanguage()).compareTo(typedOther.isSetILanguage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetILanguage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iLanguage, typedOther.iLanguage);
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
        case 1: // I_MY_EDU_SESSION
          if (field.type == org.apache.thrift.protocol.TType.STRUCT) {
            this.iMyEduSession = new MyEduSession();
            this.iMyEduSession.read(iprot);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // I_LANGUAGE
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.iLanguage = iprot.readString();
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
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.iMyEduSession != null) {
      oprot.writeFieldBegin(I_MY_EDU_SESSION_FIELD_DESC);
      this.iMyEduSession.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.iLanguage != null) {
      if (isSetILanguage()) {
        oprot.writeFieldBegin(I_LANGUAGE_FIELD_DESC);
        oprot.writeString(this.iLanguage);
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("MyEduRequest(");
    boolean first = true;

    sb.append("iMyEduSession:");
    if (this.iMyEduSession == null) {
      sb.append("null");
    } else {
      sb.append(this.iMyEduSession);
    }
    first = false;
    if (isSetILanguage()) {
      if (!first) sb.append(", ");
      sb.append("iLanguage:");
      if (this.iLanguage == null) {
        sb.append("null");
      } else {
        sb.append(this.iLanguage);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (iMyEduSession == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iMyEduSession' was not present! Struct: " + toString());
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

}

