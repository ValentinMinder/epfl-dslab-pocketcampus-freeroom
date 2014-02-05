/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.satellite.shared;

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

public class BeersResponse implements org.apache.thrift.TBase<BeersResponse, BeersResponse._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("BeersResponse");

  private static final org.apache.thrift.protocol.TField BEER_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("beerList", org.apache.thrift.protocol.TType.MAP, (short)1);
  private static final org.apache.thrift.protocol.TField STATUS_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("statusCode", org.apache.thrift.protocol.TType.I32, (short)2);

  private Map<SatelliteBeerContainer,SatelliteMenuPart> beerList; // required
  private SatelliteStatusCode statusCode; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    BEER_LIST((short)1, "beerList"),
    /**
     * 
     * @see SatelliteStatusCode
     */
    STATUS_CODE((short)2, "statusCode");

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
        case 1: // BEER_LIST
          return BEER_LIST;
        case 2: // STATUS_CODE
          return STATUS_CODE;
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
    tmpMap.put(_Fields.BEER_LIST, new org.apache.thrift.meta_data.FieldMetaData("beerList", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, SatelliteBeerContainer.class), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, SatelliteMenuPart.class))));
    tmpMap.put(_Fields.STATUS_CODE, new org.apache.thrift.meta_data.FieldMetaData("statusCode", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, SatelliteStatusCode.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(BeersResponse.class, metaDataMap);
  }

  public BeersResponse() {
  }

  public BeersResponse(
    SatelliteStatusCode statusCode)
  {
    this();
    this.statusCode = statusCode;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public BeersResponse(BeersResponse other) {
    if (other.isSetBeerList()) {
      Map<SatelliteBeerContainer,SatelliteMenuPart> __this__beerList = new HashMap<SatelliteBeerContainer,SatelliteMenuPart>();
      for (Map.Entry<SatelliteBeerContainer, SatelliteMenuPart> other_element : other.beerList.entrySet()) {

        SatelliteBeerContainer other_element_key = other_element.getKey();
        SatelliteMenuPart other_element_value = other_element.getValue();

        SatelliteBeerContainer __this__beerList_copy_key = other_element_key;

        SatelliteMenuPart __this__beerList_copy_value = new SatelliteMenuPart(other_element_value);

        __this__beerList.put(__this__beerList_copy_key, __this__beerList_copy_value);
      }
      this.beerList = __this__beerList;
    }
    if (other.isSetStatusCode()) {
      this.statusCode = other.statusCode;
    }
  }

  public BeersResponse deepCopy() {
    return new BeersResponse(this);
  }

  @Override
  public void clear() {
    this.beerList = null;
    this.statusCode = null;
  }

  public int getBeerListSize() {
    return (this.beerList == null) ? 0 : this.beerList.size();
  }

  public void putToBeerList(SatelliteBeerContainer key, SatelliteMenuPart val) {
    if (this.beerList == null) {
      this.beerList = new HashMap<SatelliteBeerContainer,SatelliteMenuPart>();
    }
    this.beerList.put(key, val);
  }

  public Map<SatelliteBeerContainer,SatelliteMenuPart> getBeerList() {
    return this.beerList;
  }

  public BeersResponse setBeerList(Map<SatelliteBeerContainer,SatelliteMenuPart> beerList) {
    this.beerList = beerList;
    return this;
  }

  public void unsetBeerList() {
    this.beerList = null;
  }

  /** Returns true if field beerList is set (has been assigned a value) and false otherwise */
  public boolean isSetBeerList() {
    return this.beerList != null;
  }

  public void setBeerListIsSet(boolean value) {
    if (!value) {
      this.beerList = null;
    }
  }

  /**
   * 
   * @see SatelliteStatusCode
   */
  public SatelliteStatusCode getStatusCode() {
    return this.statusCode;
  }

  /**
   * 
   * @see SatelliteStatusCode
   */
  public BeersResponse setStatusCode(SatelliteStatusCode statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public void unsetStatusCode() {
    this.statusCode = null;
  }

  /** Returns true if field statusCode is set (has been assigned a value) and false otherwise */
  public boolean isSetStatusCode() {
    return this.statusCode != null;
  }

  public void setStatusCodeIsSet(boolean value) {
    if (!value) {
      this.statusCode = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case BEER_LIST:
      if (value == null) {
        unsetBeerList();
      } else {
        setBeerList((Map<SatelliteBeerContainer,SatelliteMenuPart>)value);
      }
      break;

    case STATUS_CODE:
      if (value == null) {
        unsetStatusCode();
      } else {
        setStatusCode((SatelliteStatusCode)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case BEER_LIST:
      return getBeerList();

    case STATUS_CODE:
      return getStatusCode();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case BEER_LIST:
      return isSetBeerList();
    case STATUS_CODE:
      return isSetStatusCode();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof BeersResponse)
      return this.equals((BeersResponse)that);
    return false;
  }

  public boolean equals(BeersResponse that) {
    if (that == null)
      return false;

    boolean this_present_beerList = true && this.isSetBeerList();
    boolean that_present_beerList = true && that.isSetBeerList();
    if (this_present_beerList || that_present_beerList) {
      if (!(this_present_beerList && that_present_beerList))
        return false;
      if (!this.beerList.equals(that.beerList))
        return false;
    }

    boolean this_present_statusCode = true && this.isSetStatusCode();
    boolean that_present_statusCode = true && that.isSetStatusCode();
    if (this_present_statusCode || that_present_statusCode) {
      if (!(this_present_statusCode && that_present_statusCode))
        return false;
      if (!this.statusCode.equals(that.statusCode))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_beerList = true && (isSetBeerList());
    builder.append(present_beerList);
    if (present_beerList)
      builder.append(beerList);

    boolean present_statusCode = true && (isSetStatusCode());
    builder.append(present_statusCode);
    if (present_statusCode)
      builder.append(statusCode.getValue());

    return builder.toHashCode();
  }

  public int compareTo(BeersResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    BeersResponse typedOther = (BeersResponse)other;

    lastComparison = Boolean.valueOf(isSetBeerList()).compareTo(typedOther.isSetBeerList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetBeerList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.beerList, typedOther.beerList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStatusCode()).compareTo(typedOther.isSetStatusCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatusCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.statusCode, typedOther.statusCode);
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
        case 1: // BEER_LIST
          if (field.type == org.apache.thrift.protocol.TType.MAP) {
            {
              org.apache.thrift.protocol.TMap _map13 = iprot.readMapBegin();
              this.beerList = new HashMap<SatelliteBeerContainer,SatelliteMenuPart>(2*_map13.size);
              for (int _i14 = 0; _i14 < _map13.size; ++_i14)
              {
                SatelliteBeerContainer _key15; // required
                SatelliteMenuPart _val16; // required
                _key15 = SatelliteBeerContainer.findByValue(iprot.readI32());
                _val16 = new SatelliteMenuPart();
                _val16.read(iprot);
                this.beerList.put(_key15, _val16);
              }
              iprot.readMapEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // STATUS_CODE
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.statusCode = SatelliteStatusCode.findByValue(iprot.readI32());
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
    if (this.beerList != null) {
      if (isSetBeerList()) {
        oprot.writeFieldBegin(BEER_LIST_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.STRUCT, this.beerList.size()));
          for (Map.Entry<SatelliteBeerContainer, SatelliteMenuPart> _iter17 : this.beerList.entrySet())
          {
            oprot.writeI32(_iter17.getKey().getValue());
            _iter17.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
    }
    if (this.statusCode != null) {
      oprot.writeFieldBegin(STATUS_CODE_FIELD_DESC);
      oprot.writeI32(this.statusCode.getValue());
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("BeersResponse(");
    boolean first = true;

    if (isSetBeerList()) {
      sb.append("beerList:");
      if (this.beerList == null) {
        sb.append("null");
      } else {
        sb.append(this.beerList);
      }
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("statusCode:");
    if (this.statusCode == null) {
      sb.append("null");
    } else {
      sb.append(this.statusCode);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (statusCode == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'statusCode' was not present! Struct: " + toString());
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

