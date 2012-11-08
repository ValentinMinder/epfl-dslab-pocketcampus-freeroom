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

public class MyEduMaterial implements org.apache.thrift.TBase<MyEduMaterial, MyEduMaterial._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MyEduMaterial");

  private static final org.apache.thrift.protocol.TField I_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("iId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField I_MODULE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("iModuleId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField I_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("iName", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField I_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("iType", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField I_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("iURL", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField I_CREATION_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("iCreationTimestamp", org.apache.thrift.protocol.TType.I64, (short)6);
  private static final org.apache.thrift.protocol.TField I_LAST_UPDATE_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("iLastUpdateTimestamp", org.apache.thrift.protocol.TType.I64, (short)7);

  public int iId; // required
  public int iModuleId; // required
  public String iName; // required
  /**
   * 
   * @see MyEduMaterialType
   */
  public MyEduMaterialType iType; // required
  public String iURL; // required
  public long iCreationTimestamp; // required
  public long iLastUpdateTimestamp; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    I_ID((short)1, "iId"),
    I_MODULE_ID((short)2, "iModuleId"),
    I_NAME((short)3, "iName"),
    /**
     * 
     * @see MyEduMaterialType
     */
    I_TYPE((short)4, "iType"),
    I_URL((short)5, "iURL"),
    I_CREATION_TIMESTAMP((short)6, "iCreationTimestamp"),
    I_LAST_UPDATE_TIMESTAMP((short)7, "iLastUpdateTimestamp");

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
        case 1: // I_ID
          return I_ID;
        case 2: // I_MODULE_ID
          return I_MODULE_ID;
        case 3: // I_NAME
          return I_NAME;
        case 4: // I_TYPE
          return I_TYPE;
        case 5: // I_URL
          return I_URL;
        case 6: // I_CREATION_TIMESTAMP
          return I_CREATION_TIMESTAMP;
        case 7: // I_LAST_UPDATE_TIMESTAMP
          return I_LAST_UPDATE_TIMESTAMP;
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
  private static final int __IID_ISSET_ID = 0;
  private static final int __IMODULEID_ISSET_ID = 1;
  private static final int __ICREATIONTIMESTAMP_ISSET_ID = 2;
  private static final int __ILASTUPDATETIMESTAMP_ISSET_ID = 3;
  private BitSet __isset_bit_vector = new BitSet(4);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.I_ID, new org.apache.thrift.meta_data.FieldMetaData("iId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_MODULE_ID, new org.apache.thrift.meta_data.FieldMetaData("iModuleId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_NAME, new org.apache.thrift.meta_data.FieldMetaData("iName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.I_TYPE, new org.apache.thrift.meta_data.FieldMetaData("iType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, MyEduMaterialType.class)));
    tmpMap.put(_Fields.I_URL, new org.apache.thrift.meta_data.FieldMetaData("iURL", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.I_CREATION_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("iCreationTimestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "timestamp")));
    tmpMap.put(_Fields.I_LAST_UPDATE_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("iLastUpdateTimestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "timestamp")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MyEduMaterial.class, metaDataMap);
  }

  public MyEduMaterial() {
  }

  public MyEduMaterial(
    int iId,
    int iModuleId,
    String iName,
    MyEduMaterialType iType,
    String iURL,
    long iCreationTimestamp,
    long iLastUpdateTimestamp)
  {
    this();
    this.iId = iId;
    setIIdIsSet(true);
    this.iModuleId = iModuleId;
    setIModuleIdIsSet(true);
    this.iName = iName;
    this.iType = iType;
    this.iURL = iURL;
    this.iCreationTimestamp = iCreationTimestamp;
    setICreationTimestampIsSet(true);
    this.iLastUpdateTimestamp = iLastUpdateTimestamp;
    setILastUpdateTimestampIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MyEduMaterial(MyEduMaterial other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.iId = other.iId;
    this.iModuleId = other.iModuleId;
    if (other.isSetIName()) {
      this.iName = other.iName;
    }
    if (other.isSetIType()) {
      this.iType = other.iType;
    }
    if (other.isSetIURL()) {
      this.iURL = other.iURL;
    }
    this.iCreationTimestamp = other.iCreationTimestamp;
    this.iLastUpdateTimestamp = other.iLastUpdateTimestamp;
  }

  public MyEduMaterial deepCopy() {
    return new MyEduMaterial(this);
  }

  @Override
  public void clear() {
    setIIdIsSet(false);
    this.iId = 0;
    setIModuleIdIsSet(false);
    this.iModuleId = 0;
    this.iName = null;
    this.iType = null;
    this.iURL = null;
    setICreationTimestampIsSet(false);
    this.iCreationTimestamp = 0;
    setILastUpdateTimestampIsSet(false);
    this.iLastUpdateTimestamp = 0;
  }

  public int getIId() {
    return this.iId;
  }

  public MyEduMaterial setIId(int iId) {
    this.iId = iId;
    setIIdIsSet(true);
    return this;
  }

  public void unsetIId() {
    __isset_bit_vector.clear(__IID_ISSET_ID);
  }

  /** Returns true if field iId is set (has been assigned a value) and false otherwise */
  public boolean isSetIId() {
    return __isset_bit_vector.get(__IID_ISSET_ID);
  }

  public void setIIdIsSet(boolean value) {
    __isset_bit_vector.set(__IID_ISSET_ID, value);
  }

  public int getIModuleId() {
    return this.iModuleId;
  }

  public MyEduMaterial setIModuleId(int iModuleId) {
    this.iModuleId = iModuleId;
    setIModuleIdIsSet(true);
    return this;
  }

  public void unsetIModuleId() {
    __isset_bit_vector.clear(__IMODULEID_ISSET_ID);
  }

  /** Returns true if field iModuleId is set (has been assigned a value) and false otherwise */
  public boolean isSetIModuleId() {
    return __isset_bit_vector.get(__IMODULEID_ISSET_ID);
  }

  public void setIModuleIdIsSet(boolean value) {
    __isset_bit_vector.set(__IMODULEID_ISSET_ID, value);
  }

  public String getIName() {
    return this.iName;
  }

  public MyEduMaterial setIName(String iName) {
    this.iName = iName;
    return this;
  }

  public void unsetIName() {
    this.iName = null;
  }

  /** Returns true if field iName is set (has been assigned a value) and false otherwise */
  public boolean isSetIName() {
    return this.iName != null;
  }

  public void setINameIsSet(boolean value) {
    if (!value) {
      this.iName = null;
    }
  }

  /**
   * 
   * @see MyEduMaterialType
   */
  public MyEduMaterialType getIType() {
    return this.iType;
  }

  /**
   * 
   * @see MyEduMaterialType
   */
  public MyEduMaterial setIType(MyEduMaterialType iType) {
    this.iType = iType;
    return this;
  }

  public void unsetIType() {
    this.iType = null;
  }

  /** Returns true if field iType is set (has been assigned a value) and false otherwise */
  public boolean isSetIType() {
    return this.iType != null;
  }

  public void setITypeIsSet(boolean value) {
    if (!value) {
      this.iType = null;
    }
  }

  public String getIURL() {
    return this.iURL;
  }

  public MyEduMaterial setIURL(String iURL) {
    this.iURL = iURL;
    return this;
  }

  public void unsetIURL() {
    this.iURL = null;
  }

  /** Returns true if field iURL is set (has been assigned a value) and false otherwise */
  public boolean isSetIURL() {
    return this.iURL != null;
  }

  public void setIURLIsSet(boolean value) {
    if (!value) {
      this.iURL = null;
    }
  }

  public long getICreationTimestamp() {
    return this.iCreationTimestamp;
  }

  public MyEduMaterial setICreationTimestamp(long iCreationTimestamp) {
    this.iCreationTimestamp = iCreationTimestamp;
    setICreationTimestampIsSet(true);
    return this;
  }

  public void unsetICreationTimestamp() {
    __isset_bit_vector.clear(__ICREATIONTIMESTAMP_ISSET_ID);
  }

  /** Returns true if field iCreationTimestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetICreationTimestamp() {
    return __isset_bit_vector.get(__ICREATIONTIMESTAMP_ISSET_ID);
  }

  public void setICreationTimestampIsSet(boolean value) {
    __isset_bit_vector.set(__ICREATIONTIMESTAMP_ISSET_ID, value);
  }

  public long getILastUpdateTimestamp() {
    return this.iLastUpdateTimestamp;
  }

  public MyEduMaterial setILastUpdateTimestamp(long iLastUpdateTimestamp) {
    this.iLastUpdateTimestamp = iLastUpdateTimestamp;
    setILastUpdateTimestampIsSet(true);
    return this;
  }

  public void unsetILastUpdateTimestamp() {
    __isset_bit_vector.clear(__ILASTUPDATETIMESTAMP_ISSET_ID);
  }

  /** Returns true if field iLastUpdateTimestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetILastUpdateTimestamp() {
    return __isset_bit_vector.get(__ILASTUPDATETIMESTAMP_ISSET_ID);
  }

  public void setILastUpdateTimestampIsSet(boolean value) {
    __isset_bit_vector.set(__ILASTUPDATETIMESTAMP_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case I_ID:
      if (value == null) {
        unsetIId();
      } else {
        setIId((Integer)value);
      }
      break;

    case I_MODULE_ID:
      if (value == null) {
        unsetIModuleId();
      } else {
        setIModuleId((Integer)value);
      }
      break;

    case I_NAME:
      if (value == null) {
        unsetIName();
      } else {
        setIName((String)value);
      }
      break;

    case I_TYPE:
      if (value == null) {
        unsetIType();
      } else {
        setIType((MyEduMaterialType)value);
      }
      break;

    case I_URL:
      if (value == null) {
        unsetIURL();
      } else {
        setIURL((String)value);
      }
      break;

    case I_CREATION_TIMESTAMP:
      if (value == null) {
        unsetICreationTimestamp();
      } else {
        setICreationTimestamp((Long)value);
      }
      break;

    case I_LAST_UPDATE_TIMESTAMP:
      if (value == null) {
        unsetILastUpdateTimestamp();
      } else {
        setILastUpdateTimestamp((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case I_ID:
      return Integer.valueOf(getIId());

    case I_MODULE_ID:
      return Integer.valueOf(getIModuleId());

    case I_NAME:
      return getIName();

    case I_TYPE:
      return getIType();

    case I_URL:
      return getIURL();

    case I_CREATION_TIMESTAMP:
      return Long.valueOf(getICreationTimestamp());

    case I_LAST_UPDATE_TIMESTAMP:
      return Long.valueOf(getILastUpdateTimestamp());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case I_ID:
      return isSetIId();
    case I_MODULE_ID:
      return isSetIModuleId();
    case I_NAME:
      return isSetIName();
    case I_TYPE:
      return isSetIType();
    case I_URL:
      return isSetIURL();
    case I_CREATION_TIMESTAMP:
      return isSetICreationTimestamp();
    case I_LAST_UPDATE_TIMESTAMP:
      return isSetILastUpdateTimestamp();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MyEduMaterial)
      return this.equals((MyEduMaterial)that);
    return false;
  }

  public boolean equals(MyEduMaterial that) {
    if (that == null)
      return false;

    boolean this_present_iId = true;
    boolean that_present_iId = true;
    if (this_present_iId || that_present_iId) {
      if (!(this_present_iId && that_present_iId))
        return false;
      if (this.iId != that.iId)
        return false;
    }

    boolean this_present_iModuleId = true;
    boolean that_present_iModuleId = true;
    if (this_present_iModuleId || that_present_iModuleId) {
      if (!(this_present_iModuleId && that_present_iModuleId))
        return false;
      if (this.iModuleId != that.iModuleId)
        return false;
    }

    boolean this_present_iName = true && this.isSetIName();
    boolean that_present_iName = true && that.isSetIName();
    if (this_present_iName || that_present_iName) {
      if (!(this_present_iName && that_present_iName))
        return false;
      if (!this.iName.equals(that.iName))
        return false;
    }

    boolean this_present_iType = true && this.isSetIType();
    boolean that_present_iType = true && that.isSetIType();
    if (this_present_iType || that_present_iType) {
      if (!(this_present_iType && that_present_iType))
        return false;
      if (!this.iType.equals(that.iType))
        return false;
    }

    boolean this_present_iURL = true && this.isSetIURL();
    boolean that_present_iURL = true && that.isSetIURL();
    if (this_present_iURL || that_present_iURL) {
      if (!(this_present_iURL && that_present_iURL))
        return false;
      if (!this.iURL.equals(that.iURL))
        return false;
    }

    boolean this_present_iCreationTimestamp = true;
    boolean that_present_iCreationTimestamp = true;
    if (this_present_iCreationTimestamp || that_present_iCreationTimestamp) {
      if (!(this_present_iCreationTimestamp && that_present_iCreationTimestamp))
        return false;
      if (this.iCreationTimestamp != that.iCreationTimestamp)
        return false;
    }

    boolean this_present_iLastUpdateTimestamp = true;
    boolean that_present_iLastUpdateTimestamp = true;
    if (this_present_iLastUpdateTimestamp || that_present_iLastUpdateTimestamp) {
      if (!(this_present_iLastUpdateTimestamp && that_present_iLastUpdateTimestamp))
        return false;
      if (this.iLastUpdateTimestamp != that.iLastUpdateTimestamp)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_iId = true;
    builder.append(present_iId);
    if (present_iId)
      builder.append(iId);

    boolean present_iModuleId = true;
    builder.append(present_iModuleId);
    if (present_iModuleId)
      builder.append(iModuleId);

    boolean present_iName = true && (isSetIName());
    builder.append(present_iName);
    if (present_iName)
      builder.append(iName);

    boolean present_iType = true && (isSetIType());
    builder.append(present_iType);
    if (present_iType)
      builder.append(iType.getValue());

    boolean present_iURL = true && (isSetIURL());
    builder.append(present_iURL);
    if (present_iURL)
      builder.append(iURL);

    boolean present_iCreationTimestamp = true;
    builder.append(present_iCreationTimestamp);
    if (present_iCreationTimestamp)
      builder.append(iCreationTimestamp);

    boolean present_iLastUpdateTimestamp = true;
    builder.append(present_iLastUpdateTimestamp);
    if (present_iLastUpdateTimestamp)
      builder.append(iLastUpdateTimestamp);

    return builder.toHashCode();
  }

  public int compareTo(MyEduMaterial other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    MyEduMaterial typedOther = (MyEduMaterial)other;

    lastComparison = Boolean.valueOf(isSetIId()).compareTo(typedOther.isSetIId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iId, typedOther.iId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIModuleId()).compareTo(typedOther.isSetIModuleId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIModuleId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iModuleId, typedOther.iModuleId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIName()).compareTo(typedOther.isSetIName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iName, typedOther.iName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIType()).compareTo(typedOther.isSetIType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iType, typedOther.iType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIURL()).compareTo(typedOther.isSetIURL());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIURL()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iURL, typedOther.iURL);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetICreationTimestamp()).compareTo(typedOther.isSetICreationTimestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetICreationTimestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iCreationTimestamp, typedOther.iCreationTimestamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetILastUpdateTimestamp()).compareTo(typedOther.isSetILastUpdateTimestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetILastUpdateTimestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iLastUpdateTimestamp, typedOther.iLastUpdateTimestamp);
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
        case 1: // I_ID
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.iId = iprot.readI32();
            setIIdIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // I_MODULE_ID
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.iModuleId = iprot.readI32();
            setIModuleIdIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // I_NAME
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.iName = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // I_TYPE
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.iType = MyEduMaterialType.findByValue(iprot.readI32());
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // I_URL
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.iURL = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 6: // I_CREATION_TIMESTAMP
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.iCreationTimestamp = iprot.readI64();
            setICreationTimestampIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 7: // I_LAST_UPDATE_TIMESTAMP
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.iLastUpdateTimestamp = iprot.readI64();
            setILastUpdateTimestampIsSet(true);
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
    if (!isSetIId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iId' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetIModuleId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iModuleId' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetICreationTimestamp()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iCreationTimestamp' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetILastUpdateTimestamp()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iLastUpdateTimestamp' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(I_ID_FIELD_DESC);
    oprot.writeI32(this.iId);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(I_MODULE_ID_FIELD_DESC);
    oprot.writeI32(this.iModuleId);
    oprot.writeFieldEnd();
    if (this.iName != null) {
      oprot.writeFieldBegin(I_NAME_FIELD_DESC);
      oprot.writeString(this.iName);
      oprot.writeFieldEnd();
    }
    if (this.iType != null) {
      oprot.writeFieldBegin(I_TYPE_FIELD_DESC);
      oprot.writeI32(this.iType.getValue());
      oprot.writeFieldEnd();
    }
    if (this.iURL != null) {
      oprot.writeFieldBegin(I_URL_FIELD_DESC);
      oprot.writeString(this.iURL);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(I_CREATION_TIMESTAMP_FIELD_DESC);
    oprot.writeI64(this.iCreationTimestamp);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(I_LAST_UPDATE_TIMESTAMP_FIELD_DESC);
    oprot.writeI64(this.iLastUpdateTimestamp);
    oprot.writeFieldEnd();
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("MyEduMaterial(");
    boolean first = true;

    sb.append("iId:");
    sb.append(this.iId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iModuleId:");
    sb.append(this.iModuleId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iName:");
    if (this.iName == null) {
      sb.append("null");
    } else {
      sb.append(this.iName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("iType:");
    if (this.iType == null) {
      sb.append("null");
    } else {
      sb.append(this.iType);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("iURL:");
    if (this.iURL == null) {
      sb.append("null");
    } else {
      sb.append(this.iURL);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("iCreationTimestamp:");
    sb.append(this.iCreationTimestamp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iLastUpdateTimestamp:");
    sb.append(this.iLastUpdateTimestamp);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'iId' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'iModuleId' because it's a primitive and you chose the non-beans generator.
    if (iName == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iName' was not present! Struct: " + toString());
    }
    if (iType == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iType' was not present! Struct: " + toString());
    }
    if (iURL == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iURL' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'iCreationTimestamp' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'iLastUpdateTimestamp' because it's a primitive and you chose the non-beans generator.
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

