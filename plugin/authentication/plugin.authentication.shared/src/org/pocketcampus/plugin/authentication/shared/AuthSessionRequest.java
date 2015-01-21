/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.pocketcampus.plugin.authentication.shared;

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
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-1-20")
public class AuthSessionRequest implements org.apache.thrift.TBase<AuthSessionRequest, AuthSessionRequest._Fields>, java.io.Serializable, Cloneable, Comparable<AuthSessionRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("AuthSessionRequest");

  private static final org.apache.thrift.protocol.TField TEQUILA_TOKEN_FIELD_DESC = new org.apache.thrift.protocol.TField("tequilaToken", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField REMEMBER_ME_FIELD_DESC = new org.apache.thrift.protocol.TField("rememberMe", org.apache.thrift.protocol.TType.BOOL, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new AuthSessionRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new AuthSessionRequestTupleSchemeFactory());
  }

  private String tequilaToken; // required
  private boolean rememberMe; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TEQUILA_TOKEN((short)1, "tequilaToken"),
    REMEMBER_ME((short)2, "rememberMe");

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
        case 1: // TEQUILA_TOKEN
          return TEQUILA_TOKEN;
        case 2: // REMEMBER_ME
          return REMEMBER_ME;
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
  private static final int __REMEMBERME_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.REMEMBER_ME};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TEQUILA_TOKEN, new org.apache.thrift.meta_data.FieldMetaData("tequilaToken", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.REMEMBER_ME, new org.apache.thrift.meta_data.FieldMetaData("rememberMe", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(AuthSessionRequest.class, metaDataMap);
  }

  public AuthSessionRequest() {
  }

  public AuthSessionRequest(
    String tequilaToken)
  {
    this();
    this.tequilaToken = tequilaToken;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public AuthSessionRequest(AuthSessionRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetTequilaToken()) {
      this.tequilaToken = other.tequilaToken;
    }
    this.rememberMe = other.rememberMe;
  }

  public AuthSessionRequest deepCopy() {
    return new AuthSessionRequest(this);
  }

  @Override
  public void clear() {
    this.tequilaToken = null;
    setRememberMeIsSet(false);
    this.rememberMe = false;
  }

  public String getTequilaToken() {
    return this.tequilaToken;
  }

  public AuthSessionRequest setTequilaToken(String tequilaToken) {
    this.tequilaToken = tequilaToken;
    return this;
  }

  public void unsetTequilaToken() {
    this.tequilaToken = null;
  }

  /** Returns true if field tequilaToken is set (has been assigned a value) and false otherwise */
  public boolean isSetTequilaToken() {
    return this.tequilaToken != null;
  }

  public void setTequilaTokenIsSet(boolean value) {
    if (!value) {
      this.tequilaToken = null;
    }
  }

  public boolean isRememberMe() {
    return this.rememberMe;
  }

  public AuthSessionRequest setRememberMe(boolean rememberMe) {
    this.rememberMe = rememberMe;
    setRememberMeIsSet(true);
    return this;
  }

  public void unsetRememberMe() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __REMEMBERME_ISSET_ID);
  }

  /** Returns true if field rememberMe is set (has been assigned a value) and false otherwise */
  public boolean isSetRememberMe() {
    return EncodingUtils.testBit(__isset_bitfield, __REMEMBERME_ISSET_ID);
  }

  public void setRememberMeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __REMEMBERME_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TEQUILA_TOKEN:
      if (value == null) {
        unsetTequilaToken();
      } else {
        setTequilaToken((String)value);
      }
      break;

    case REMEMBER_ME:
      if (value == null) {
        unsetRememberMe();
      } else {
        setRememberMe((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TEQUILA_TOKEN:
      return getTequilaToken();

    case REMEMBER_ME:
      return Boolean.valueOf(isRememberMe());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TEQUILA_TOKEN:
      return isSetTequilaToken();
    case REMEMBER_ME:
      return isSetRememberMe();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof AuthSessionRequest)
      return this.equals((AuthSessionRequest)that);
    return false;
  }

  public boolean equals(AuthSessionRequest that) {
    if (that == null)
      return false;

    boolean this_present_tequilaToken = true && this.isSetTequilaToken();
    boolean that_present_tequilaToken = true && that.isSetTequilaToken();
    if (this_present_tequilaToken || that_present_tequilaToken) {
      if (!(this_present_tequilaToken && that_present_tequilaToken))
        return false;
      if (!this.tequilaToken.equals(that.tequilaToken))
        return false;
    }

    boolean this_present_rememberMe = true && this.isSetRememberMe();
    boolean that_present_rememberMe = true && that.isSetRememberMe();
    if (this_present_rememberMe || that_present_rememberMe) {
      if (!(this_present_rememberMe && that_present_rememberMe))
        return false;
      if (this.rememberMe != that.rememberMe)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_tequilaToken = true && (isSetTequilaToken());
    list.add(present_tequilaToken);
    if (present_tequilaToken)
      list.add(tequilaToken);

    boolean present_rememberMe = true && (isSetRememberMe());
    list.add(present_rememberMe);
    if (present_rememberMe)
      list.add(rememberMe);

    return list.hashCode();
  }

  @Override
  public int compareTo(AuthSessionRequest other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetTequilaToken()).compareTo(other.isSetTequilaToken());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTequilaToken()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tequilaToken, other.tequilaToken);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRememberMe()).compareTo(other.isSetRememberMe());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRememberMe()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rememberMe, other.rememberMe);
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
    StringBuilder sb = new StringBuilder("AuthSessionRequest(");
    boolean first = true;

    sb.append("tequilaToken:");
    if (this.tequilaToken == null) {
      sb.append("null");
    } else {
      sb.append(this.tequilaToken);
    }
    first = false;
    if (isSetRememberMe()) {
      if (!first) sb.append(", ");
      sb.append("rememberMe:");
      sb.append(this.rememberMe);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (tequilaToken == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'tequilaToken' was not present! Struct: " + toString());
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

  private static class AuthSessionRequestStandardSchemeFactory implements SchemeFactory {
    public AuthSessionRequestStandardScheme getScheme() {
      return new AuthSessionRequestStandardScheme();
    }
  }

  private static class AuthSessionRequestStandardScheme extends StandardScheme<AuthSessionRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, AuthSessionRequest struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // TEQUILA_TOKEN
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.tequilaToken = iprot.readString();
              struct.setTequilaTokenIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // REMEMBER_ME
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.rememberMe = iprot.readBool();
              struct.setRememberMeIsSet(true);
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
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, AuthSessionRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.tequilaToken != null) {
        oprot.writeFieldBegin(TEQUILA_TOKEN_FIELD_DESC);
        oprot.writeString(struct.tequilaToken);
        oprot.writeFieldEnd();
      }
      if (struct.isSetRememberMe()) {
        oprot.writeFieldBegin(REMEMBER_ME_FIELD_DESC);
        oprot.writeBool(struct.rememberMe);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class AuthSessionRequestTupleSchemeFactory implements SchemeFactory {
    public AuthSessionRequestTupleScheme getScheme() {
      return new AuthSessionRequestTupleScheme();
    }
  }

  private static class AuthSessionRequestTupleScheme extends TupleScheme<AuthSessionRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, AuthSessionRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.tequilaToken);
      BitSet optionals = new BitSet();
      if (struct.isSetRememberMe()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetRememberMe()) {
        oprot.writeBool(struct.rememberMe);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, AuthSessionRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.tequilaToken = iprot.readString();
      struct.setTequilaTokenIsSet(true);
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.rememberMe = iprot.readBool();
        struct.setRememberMeIsSet(true);
      }
    }
  }

}

