/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.pocketcampus.plugin.directory.shared;

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
public class DirectoryResponse implements org.apache.thrift.TBase<DirectoryResponse, DirectoryResponse._Fields>, java.io.Serializable, Cloneable, Comparable<DirectoryResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DirectoryResponse");

  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField RESULTS_FIELD_DESC = new org.apache.thrift.protocol.TField("results", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField RESULT_SET_COOKIE_FIELD_DESC = new org.apache.thrift.protocol.TField("resultSetCookie", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new DirectoryResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new DirectoryResponseTupleSchemeFactory());
  }

  private int status; // required
  private List<Person> results; // optional
  private ByteBuffer resultSetCookie; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    STATUS((short)1, "status"),
    RESULTS((short)2, "results"),
    RESULT_SET_COOKIE((short)3, "resultSetCookie");

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
        case 1: // STATUS
          return STATUS;
        case 2: // RESULTS
          return RESULTS;
        case 3: // RESULT_SET_COOKIE
          return RESULT_SET_COOKIE;
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
  private static final int __STATUS_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.RESULTS,_Fields.RESULT_SET_COOKIE};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.RESULTS, new org.apache.thrift.meta_data.FieldMetaData("results", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Person.class))));
    tmpMap.put(_Fields.RESULT_SET_COOKIE, new org.apache.thrift.meta_data.FieldMetaData("resultSetCookie", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DirectoryResponse.class, metaDataMap);
  }

  public DirectoryResponse() {
  }

  public DirectoryResponse(
    int status)
  {
    this();
    this.status = status;
    setStatusIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DirectoryResponse(DirectoryResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    this.status = other.status;
    if (other.isSetResults()) {
      List<Person> __this__results = new ArrayList<Person>(other.results.size());
      for (Person other_element : other.results) {
        __this__results.add(new Person(other_element));
      }
      this.results = __this__results;
    }
    if (other.isSetResultSetCookie()) {
      this.resultSetCookie = org.apache.thrift.TBaseHelper.copyBinary(other.resultSetCookie);
    }
  }

  public DirectoryResponse deepCopy() {
    return new DirectoryResponse(this);
  }

  @Override
  public void clear() {
    setStatusIsSet(false);
    this.status = 0;
    this.results = null;
    this.resultSetCookie = null;
  }

  public int getStatus() {
    return this.status;
  }

  public DirectoryResponse setStatus(int status) {
    this.status = status;
    setStatusIsSet(true);
    return this;
  }

  public void unsetStatus() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __STATUS_ISSET_ID);
  }

  /** Returns true if field status is set (has been assigned a value) and false otherwise */
  public boolean isSetStatus() {
    return EncodingUtils.testBit(__isset_bitfield, __STATUS_ISSET_ID);
  }

  public void setStatusIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __STATUS_ISSET_ID, value);
  }

  public int getResultsSize() {
    return (this.results == null) ? 0 : this.results.size();
  }

  public java.util.Iterator<Person> getResultsIterator() {
    return (this.results == null) ? null : this.results.iterator();
  }

  public void addToResults(Person elem) {
    if (this.results == null) {
      this.results = new ArrayList<Person>();
    }
    this.results.add(elem);
  }

  public List<Person> getResults() {
    return this.results;
  }

  public DirectoryResponse setResults(List<Person> results) {
    this.results = results;
    return this;
  }

  public void unsetResults() {
    this.results = null;
  }

  /** Returns true if field results is set (has been assigned a value) and false otherwise */
  public boolean isSetResults() {
    return this.results != null;
  }

  public void setResultsIsSet(boolean value) {
    if (!value) {
      this.results = null;
    }
  }

  public byte[] getResultSetCookie() {
    setResultSetCookie(org.apache.thrift.TBaseHelper.rightSize(resultSetCookie));
    return resultSetCookie == null ? null : resultSetCookie.array();
  }

  public ByteBuffer bufferForResultSetCookie() {
    return org.apache.thrift.TBaseHelper.copyBinary(resultSetCookie);
  }

  public DirectoryResponse setResultSetCookie(byte[] resultSetCookie) {
    this.resultSetCookie = resultSetCookie == null ? (ByteBuffer)null : ByteBuffer.wrap(Arrays.copyOf(resultSetCookie, resultSetCookie.length));
    return this;
  }

  public DirectoryResponse setResultSetCookie(ByteBuffer resultSetCookie) {
    this.resultSetCookie = org.apache.thrift.TBaseHelper.copyBinary(resultSetCookie);
    return this;
  }

  public void unsetResultSetCookie() {
    this.resultSetCookie = null;
  }

  /** Returns true if field resultSetCookie is set (has been assigned a value) and false otherwise */
  public boolean isSetResultSetCookie() {
    return this.resultSetCookie != null;
  }

  public void setResultSetCookieIsSet(boolean value) {
    if (!value) {
      this.resultSetCookie = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((Integer)value);
      }
      break;

    case RESULTS:
      if (value == null) {
        unsetResults();
      } else {
        setResults((List<Person>)value);
      }
      break;

    case RESULT_SET_COOKIE:
      if (value == null) {
        unsetResultSetCookie();
      } else {
        setResultSetCookie((ByteBuffer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case STATUS:
      return Integer.valueOf(getStatus());

    case RESULTS:
      return getResults();

    case RESULT_SET_COOKIE:
      return getResultSetCookie();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case STATUS:
      return isSetStatus();
    case RESULTS:
      return isSetResults();
    case RESULT_SET_COOKIE:
      return isSetResultSetCookie();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof DirectoryResponse)
      return this.equals((DirectoryResponse)that);
    return false;
  }

  public boolean equals(DirectoryResponse that) {
    if (that == null)
      return false;

    boolean this_present_status = true;
    boolean that_present_status = true;
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (this.status != that.status)
        return false;
    }

    boolean this_present_results = true && this.isSetResults();
    boolean that_present_results = true && that.isSetResults();
    if (this_present_results || that_present_results) {
      if (!(this_present_results && that_present_results))
        return false;
      if (!this.results.equals(that.results))
        return false;
    }

    boolean this_present_resultSetCookie = true && this.isSetResultSetCookie();
    boolean that_present_resultSetCookie = true && that.isSetResultSetCookie();
    if (this_present_resultSetCookie || that_present_resultSetCookie) {
      if (!(this_present_resultSetCookie && that_present_resultSetCookie))
        return false;
      if (!this.resultSetCookie.equals(that.resultSetCookie))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_status = true;
    list.add(present_status);
    if (present_status)
      list.add(status);

    boolean present_results = true && (isSetResults());
    list.add(present_results);
    if (present_results)
      list.add(results);

    boolean present_resultSetCookie = true && (isSetResultSetCookie());
    list.add(present_resultSetCookie);
    if (present_resultSetCookie)
      list.add(resultSetCookie);

    return list.hashCode();
  }

  @Override
  public int compareTo(DirectoryResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetStatus()).compareTo(other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.status, other.status);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetResults()).compareTo(other.isSetResults());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResults()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.results, other.results);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetResultSetCookie()).compareTo(other.isSetResultSetCookie());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResultSetCookie()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resultSetCookie, other.resultSetCookie);
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
    StringBuilder sb = new StringBuilder("DirectoryResponse(");
    boolean first = true;

    sb.append("status:");
    sb.append(this.status);
    first = false;
    if (isSetResults()) {
      if (!first) sb.append(", ");
      sb.append("results:");
      if (this.results == null) {
        sb.append("null");
      } else {
        sb.append(this.results);
      }
      first = false;
    }
    if (isSetResultSetCookie()) {
      if (!first) sb.append(", ");
      sb.append("resultSetCookie:");
      if (this.resultSetCookie == null) {
        sb.append("null");
      } else {
        org.apache.thrift.TBaseHelper.toString(this.resultSetCookie, sb);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'status' because it's a primitive and you chose the non-beans generator.
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

  private static class DirectoryResponseStandardSchemeFactory implements SchemeFactory {
    public DirectoryResponseStandardScheme getScheme() {
      return new DirectoryResponseStandardScheme();
    }
  }

  private static class DirectoryResponseStandardScheme extends StandardScheme<DirectoryResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, DirectoryResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.status = iprot.readI32();
              struct.setStatusIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // RESULTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list28 = iprot.readListBegin();
                struct.results = new ArrayList<Person>(_list28.size);
                Person _elem29;
                for (int _i30 = 0; _i30 < _list28.size; ++_i30)
                {
                  _elem29 = new Person();
                  _elem29.read(iprot);
                  struct.results.add(_elem29);
                }
                iprot.readListEnd();
              }
              struct.setResultsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // RESULT_SET_COOKIE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.resultSetCookie = iprot.readBinary();
              struct.setResultSetCookieIsSet(true);
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
      if (!struct.isSetStatus()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'status' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, DirectoryResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(STATUS_FIELD_DESC);
      oprot.writeI32(struct.status);
      oprot.writeFieldEnd();
      if (struct.results != null) {
        if (struct.isSetResults()) {
          oprot.writeFieldBegin(RESULTS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.results.size()));
            for (Person _iter31 : struct.results)
            {
              _iter31.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.resultSetCookie != null) {
        if (struct.isSetResultSetCookie()) {
          oprot.writeFieldBegin(RESULT_SET_COOKIE_FIELD_DESC);
          oprot.writeBinary(struct.resultSetCookie);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DirectoryResponseTupleSchemeFactory implements SchemeFactory {
    public DirectoryResponseTupleScheme getScheme() {
      return new DirectoryResponseTupleScheme();
    }
  }

  private static class DirectoryResponseTupleScheme extends TupleScheme<DirectoryResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, DirectoryResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.status);
      BitSet optionals = new BitSet();
      if (struct.isSetResults()) {
        optionals.set(0);
      }
      if (struct.isSetResultSetCookie()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetResults()) {
        {
          oprot.writeI32(struct.results.size());
          for (Person _iter32 : struct.results)
          {
            _iter32.write(oprot);
          }
        }
      }
      if (struct.isSetResultSetCookie()) {
        oprot.writeBinary(struct.resultSetCookie);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, DirectoryResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.status = iprot.readI32();
      struct.setStatusIsSet(true);
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list33 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.results = new ArrayList<Person>(_list33.size);
          Person _elem34;
          for (int _i35 = 0; _i35 < _list33.size; ++_i35)
          {
            _elem34 = new Person();
            _elem34.read(iprot);
            struct.results.add(_elem34);
          }
        }
        struct.setResultsIsSet(true);
      }
      if (incoming.get(1)) {
        struct.resultSetCookie = iprot.readBinary();
        struct.setResultSetCookieIsSet(true);
      }
    }
  }

}

