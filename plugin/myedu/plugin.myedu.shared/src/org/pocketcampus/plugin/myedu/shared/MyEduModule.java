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

public class MyEduModule implements org.apache.thrift.TBase<MyEduModule, MyEduModule._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MyEduModule");

  private static final org.apache.thrift.protocol.TField I_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("iId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField I_SECTION_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("iSectionId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField I_SEQUENCE_FIELD_DESC = new org.apache.thrift.protocol.TField("iSequence", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField I_VISIBLE_FIELD_DESC = new org.apache.thrift.protocol.TField("iVisible", org.apache.thrift.protocol.TType.BOOL, (short)4);
  private static final org.apache.thrift.protocol.TField I_TEXT_CONTENT_FIELD_DESC = new org.apache.thrift.protocol.TField("iTextContent", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField I_VIDEO_SOURCE_PROVIDER_FIELD_DESC = new org.apache.thrift.protocol.TField("iVideoSourceProvider", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField I_VIDEO_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("iVideoURL", org.apache.thrift.protocol.TType.STRING, (short)7);
  private static final org.apache.thrift.protocol.TField I_CREATION_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("iCreationTimestamp", org.apache.thrift.protocol.TType.I64, (short)8);
  private static final org.apache.thrift.protocol.TField I_LAST_UPDATE_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("iLastUpdateTimestamp", org.apache.thrift.protocol.TType.I64, (short)9);

  public int iId; // required
  public int iSectionId; // required
  public int iSequence; // required
  public boolean iVisible; // required
  public String iTextContent; // required
  public String iVideoSourceProvider; // required
  public String iVideoURL; // required
  public long iCreationTimestamp; // required
  public long iLastUpdateTimestamp; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    I_ID((short)1, "iId"),
    I_SECTION_ID((short)2, "iSectionId"),
    I_SEQUENCE((short)3, "iSequence"),
    I_VISIBLE((short)4, "iVisible"),
    I_TEXT_CONTENT((short)5, "iTextContent"),
    I_VIDEO_SOURCE_PROVIDER((short)6, "iVideoSourceProvider"),
    I_VIDEO_URL((short)7, "iVideoURL"),
    I_CREATION_TIMESTAMP((short)8, "iCreationTimestamp"),
    I_LAST_UPDATE_TIMESTAMP((short)9, "iLastUpdateTimestamp");

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
        case 2: // I_SECTION_ID
          return I_SECTION_ID;
        case 3: // I_SEQUENCE
          return I_SEQUENCE;
        case 4: // I_VISIBLE
          return I_VISIBLE;
        case 5: // I_TEXT_CONTENT
          return I_TEXT_CONTENT;
        case 6: // I_VIDEO_SOURCE_PROVIDER
          return I_VIDEO_SOURCE_PROVIDER;
        case 7: // I_VIDEO_URL
          return I_VIDEO_URL;
        case 8: // I_CREATION_TIMESTAMP
          return I_CREATION_TIMESTAMP;
        case 9: // I_LAST_UPDATE_TIMESTAMP
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
  private static final int __ISECTIONID_ISSET_ID = 1;
  private static final int __ISEQUENCE_ISSET_ID = 2;
  private static final int __IVISIBLE_ISSET_ID = 3;
  private static final int __ICREATIONTIMESTAMP_ISSET_ID = 4;
  private static final int __ILASTUPDATETIMESTAMP_ISSET_ID = 5;
  private BitSet __isset_bit_vector = new BitSet(6);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.I_ID, new org.apache.thrift.meta_data.FieldMetaData("iId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_SECTION_ID, new org.apache.thrift.meta_data.FieldMetaData("iSectionId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_SEQUENCE, new org.apache.thrift.meta_data.FieldMetaData("iSequence", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_VISIBLE, new org.apache.thrift.meta_data.FieldMetaData("iVisible", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.I_TEXT_CONTENT, new org.apache.thrift.meta_data.FieldMetaData("iTextContent", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.I_VIDEO_SOURCE_PROVIDER, new org.apache.thrift.meta_data.FieldMetaData("iVideoSourceProvider", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.I_VIDEO_URL, new org.apache.thrift.meta_data.FieldMetaData("iVideoURL", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.I_CREATION_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("iCreationTimestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "timestamp")));
    tmpMap.put(_Fields.I_LAST_UPDATE_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("iLastUpdateTimestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "timestamp")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MyEduModule.class, metaDataMap);
  }

  public MyEduModule() {
  }

  public MyEduModule(
    int iId,
    int iSectionId,
    int iSequence,
    boolean iVisible,
    String iTextContent,
    String iVideoSourceProvider,
    String iVideoURL,
    long iCreationTimestamp,
    long iLastUpdateTimestamp)
  {
    this();
    this.iId = iId;
    setIIdIsSet(true);
    this.iSectionId = iSectionId;
    setISectionIdIsSet(true);
    this.iSequence = iSequence;
    setISequenceIsSet(true);
    this.iVisible = iVisible;
    setIVisibleIsSet(true);
    this.iTextContent = iTextContent;
    this.iVideoSourceProvider = iVideoSourceProvider;
    this.iVideoURL = iVideoURL;
    this.iCreationTimestamp = iCreationTimestamp;
    setICreationTimestampIsSet(true);
    this.iLastUpdateTimestamp = iLastUpdateTimestamp;
    setILastUpdateTimestampIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MyEduModule(MyEduModule other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.iId = other.iId;
    this.iSectionId = other.iSectionId;
    this.iSequence = other.iSequence;
    this.iVisible = other.iVisible;
    if (other.isSetITextContent()) {
      this.iTextContent = other.iTextContent;
    }
    if (other.isSetIVideoSourceProvider()) {
      this.iVideoSourceProvider = other.iVideoSourceProvider;
    }
    if (other.isSetIVideoURL()) {
      this.iVideoURL = other.iVideoURL;
    }
    this.iCreationTimestamp = other.iCreationTimestamp;
    this.iLastUpdateTimestamp = other.iLastUpdateTimestamp;
  }

  public MyEduModule deepCopy() {
    return new MyEduModule(this);
  }

  @Override
  public void clear() {
    setIIdIsSet(false);
    this.iId = 0;
    setISectionIdIsSet(false);
    this.iSectionId = 0;
    setISequenceIsSet(false);
    this.iSequence = 0;
    setIVisibleIsSet(false);
    this.iVisible = false;
    this.iTextContent = null;
    this.iVideoSourceProvider = null;
    this.iVideoURL = null;
    setICreationTimestampIsSet(false);
    this.iCreationTimestamp = 0;
    setILastUpdateTimestampIsSet(false);
    this.iLastUpdateTimestamp = 0;
  }

  public int getIId() {
    return this.iId;
  }

  public MyEduModule setIId(int iId) {
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

  public int getISectionId() {
    return this.iSectionId;
  }

  public MyEduModule setISectionId(int iSectionId) {
    this.iSectionId = iSectionId;
    setISectionIdIsSet(true);
    return this;
  }

  public void unsetISectionId() {
    __isset_bit_vector.clear(__ISECTIONID_ISSET_ID);
  }

  /** Returns true if field iSectionId is set (has been assigned a value) and false otherwise */
  public boolean isSetISectionId() {
    return __isset_bit_vector.get(__ISECTIONID_ISSET_ID);
  }

  public void setISectionIdIsSet(boolean value) {
    __isset_bit_vector.set(__ISECTIONID_ISSET_ID, value);
  }

  public int getISequence() {
    return this.iSequence;
  }

  public MyEduModule setISequence(int iSequence) {
    this.iSequence = iSequence;
    setISequenceIsSet(true);
    return this;
  }

  public void unsetISequence() {
    __isset_bit_vector.clear(__ISEQUENCE_ISSET_ID);
  }

  /** Returns true if field iSequence is set (has been assigned a value) and false otherwise */
  public boolean isSetISequence() {
    return __isset_bit_vector.get(__ISEQUENCE_ISSET_ID);
  }

  public void setISequenceIsSet(boolean value) {
    __isset_bit_vector.set(__ISEQUENCE_ISSET_ID, value);
  }

  public boolean isIVisible() {
    return this.iVisible;
  }

  public MyEduModule setIVisible(boolean iVisible) {
    this.iVisible = iVisible;
    setIVisibleIsSet(true);
    return this;
  }

  public void unsetIVisible() {
    __isset_bit_vector.clear(__IVISIBLE_ISSET_ID);
  }

  /** Returns true if field iVisible is set (has been assigned a value) and false otherwise */
  public boolean isSetIVisible() {
    return __isset_bit_vector.get(__IVISIBLE_ISSET_ID);
  }

  public void setIVisibleIsSet(boolean value) {
    __isset_bit_vector.set(__IVISIBLE_ISSET_ID, value);
  }

  public String getITextContent() {
    return this.iTextContent;
  }

  public MyEduModule setITextContent(String iTextContent) {
    this.iTextContent = iTextContent;
    return this;
  }

  public void unsetITextContent() {
    this.iTextContent = null;
  }

  /** Returns true if field iTextContent is set (has been assigned a value) and false otherwise */
  public boolean isSetITextContent() {
    return this.iTextContent != null;
  }

  public void setITextContentIsSet(boolean value) {
    if (!value) {
      this.iTextContent = null;
    }
  }

  public String getIVideoSourceProvider() {
    return this.iVideoSourceProvider;
  }

  public MyEduModule setIVideoSourceProvider(String iVideoSourceProvider) {
    this.iVideoSourceProvider = iVideoSourceProvider;
    return this;
  }

  public void unsetIVideoSourceProvider() {
    this.iVideoSourceProvider = null;
  }

  /** Returns true if field iVideoSourceProvider is set (has been assigned a value) and false otherwise */
  public boolean isSetIVideoSourceProvider() {
    return this.iVideoSourceProvider != null;
  }

  public void setIVideoSourceProviderIsSet(boolean value) {
    if (!value) {
      this.iVideoSourceProvider = null;
    }
  }

  public String getIVideoURL() {
    return this.iVideoURL;
  }

  public MyEduModule setIVideoURL(String iVideoURL) {
    this.iVideoURL = iVideoURL;
    return this;
  }

  public void unsetIVideoURL() {
    this.iVideoURL = null;
  }

  /** Returns true if field iVideoURL is set (has been assigned a value) and false otherwise */
  public boolean isSetIVideoURL() {
    return this.iVideoURL != null;
  }

  public void setIVideoURLIsSet(boolean value) {
    if (!value) {
      this.iVideoURL = null;
    }
  }

  public long getICreationTimestamp() {
    return this.iCreationTimestamp;
  }

  public MyEduModule setICreationTimestamp(long iCreationTimestamp) {
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

  public MyEduModule setILastUpdateTimestamp(long iLastUpdateTimestamp) {
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

    case I_SECTION_ID:
      if (value == null) {
        unsetISectionId();
      } else {
        setISectionId((Integer)value);
      }
      break;

    case I_SEQUENCE:
      if (value == null) {
        unsetISequence();
      } else {
        setISequence((Integer)value);
      }
      break;

    case I_VISIBLE:
      if (value == null) {
        unsetIVisible();
      } else {
        setIVisible((Boolean)value);
      }
      break;

    case I_TEXT_CONTENT:
      if (value == null) {
        unsetITextContent();
      } else {
        setITextContent((String)value);
      }
      break;

    case I_VIDEO_SOURCE_PROVIDER:
      if (value == null) {
        unsetIVideoSourceProvider();
      } else {
        setIVideoSourceProvider((String)value);
      }
      break;

    case I_VIDEO_URL:
      if (value == null) {
        unsetIVideoURL();
      } else {
        setIVideoURL((String)value);
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

    case I_SECTION_ID:
      return Integer.valueOf(getISectionId());

    case I_SEQUENCE:
      return Integer.valueOf(getISequence());

    case I_VISIBLE:
      return Boolean.valueOf(isIVisible());

    case I_TEXT_CONTENT:
      return getITextContent();

    case I_VIDEO_SOURCE_PROVIDER:
      return getIVideoSourceProvider();

    case I_VIDEO_URL:
      return getIVideoURL();

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
    case I_SECTION_ID:
      return isSetISectionId();
    case I_SEQUENCE:
      return isSetISequence();
    case I_VISIBLE:
      return isSetIVisible();
    case I_TEXT_CONTENT:
      return isSetITextContent();
    case I_VIDEO_SOURCE_PROVIDER:
      return isSetIVideoSourceProvider();
    case I_VIDEO_URL:
      return isSetIVideoURL();
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
    if (that instanceof MyEduModule)
      return this.equals((MyEduModule)that);
    return false;
  }

  public boolean equals(MyEduModule that) {
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

    boolean this_present_iSectionId = true;
    boolean that_present_iSectionId = true;
    if (this_present_iSectionId || that_present_iSectionId) {
      if (!(this_present_iSectionId && that_present_iSectionId))
        return false;
      if (this.iSectionId != that.iSectionId)
        return false;
    }

    boolean this_present_iSequence = true;
    boolean that_present_iSequence = true;
    if (this_present_iSequence || that_present_iSequence) {
      if (!(this_present_iSequence && that_present_iSequence))
        return false;
      if (this.iSequence != that.iSequence)
        return false;
    }

    boolean this_present_iVisible = true;
    boolean that_present_iVisible = true;
    if (this_present_iVisible || that_present_iVisible) {
      if (!(this_present_iVisible && that_present_iVisible))
        return false;
      if (this.iVisible != that.iVisible)
        return false;
    }

    boolean this_present_iTextContent = true && this.isSetITextContent();
    boolean that_present_iTextContent = true && that.isSetITextContent();
    if (this_present_iTextContent || that_present_iTextContent) {
      if (!(this_present_iTextContent && that_present_iTextContent))
        return false;
      if (!this.iTextContent.equals(that.iTextContent))
        return false;
    }

    boolean this_present_iVideoSourceProvider = true && this.isSetIVideoSourceProvider();
    boolean that_present_iVideoSourceProvider = true && that.isSetIVideoSourceProvider();
    if (this_present_iVideoSourceProvider || that_present_iVideoSourceProvider) {
      if (!(this_present_iVideoSourceProvider && that_present_iVideoSourceProvider))
        return false;
      if (!this.iVideoSourceProvider.equals(that.iVideoSourceProvider))
        return false;
    }

    boolean this_present_iVideoURL = true && this.isSetIVideoURL();
    boolean that_present_iVideoURL = true && that.isSetIVideoURL();
    if (this_present_iVideoURL || that_present_iVideoURL) {
      if (!(this_present_iVideoURL && that_present_iVideoURL))
        return false;
      if (!this.iVideoURL.equals(that.iVideoURL))
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

    boolean present_iSectionId = true;
    builder.append(present_iSectionId);
    if (present_iSectionId)
      builder.append(iSectionId);

    boolean present_iSequence = true;
    builder.append(present_iSequence);
    if (present_iSequence)
      builder.append(iSequence);

    boolean present_iVisible = true;
    builder.append(present_iVisible);
    if (present_iVisible)
      builder.append(iVisible);

    boolean present_iTextContent = true && (isSetITextContent());
    builder.append(present_iTextContent);
    if (present_iTextContent)
      builder.append(iTextContent);

    boolean present_iVideoSourceProvider = true && (isSetIVideoSourceProvider());
    builder.append(present_iVideoSourceProvider);
    if (present_iVideoSourceProvider)
      builder.append(iVideoSourceProvider);

    boolean present_iVideoURL = true && (isSetIVideoURL());
    builder.append(present_iVideoURL);
    if (present_iVideoURL)
      builder.append(iVideoURL);

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

  public int compareTo(MyEduModule other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    MyEduModule typedOther = (MyEduModule)other;

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
    lastComparison = Boolean.valueOf(isSetISectionId()).compareTo(typedOther.isSetISectionId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetISectionId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iSectionId, typedOther.iSectionId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetISequence()).compareTo(typedOther.isSetISequence());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetISequence()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iSequence, typedOther.iSequence);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIVisible()).compareTo(typedOther.isSetIVisible());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIVisible()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iVisible, typedOther.iVisible);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetITextContent()).compareTo(typedOther.isSetITextContent());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetITextContent()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iTextContent, typedOther.iTextContent);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIVideoSourceProvider()).compareTo(typedOther.isSetIVideoSourceProvider());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIVideoSourceProvider()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iVideoSourceProvider, typedOther.iVideoSourceProvider);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIVideoURL()).compareTo(typedOther.isSetIVideoURL());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIVideoURL()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iVideoURL, typedOther.iVideoURL);
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
        case 2: // I_SECTION_ID
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.iSectionId = iprot.readI32();
            setISectionIdIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // I_SEQUENCE
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.iSequence = iprot.readI32();
            setISequenceIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // I_VISIBLE
          if (field.type == org.apache.thrift.protocol.TType.BOOL) {
            this.iVisible = iprot.readBool();
            setIVisibleIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // I_TEXT_CONTENT
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.iTextContent = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 6: // I_VIDEO_SOURCE_PROVIDER
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.iVideoSourceProvider = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 7: // I_VIDEO_URL
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.iVideoURL = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 8: // I_CREATION_TIMESTAMP
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.iCreationTimestamp = iprot.readI64();
            setICreationTimestampIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 9: // I_LAST_UPDATE_TIMESTAMP
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
    if (!isSetISectionId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iSectionId' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetISequence()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iSequence' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetIVisible()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iVisible' was not found in serialized data! Struct: " + toString());
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
    oprot.writeFieldBegin(I_SECTION_ID_FIELD_DESC);
    oprot.writeI32(this.iSectionId);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(I_SEQUENCE_FIELD_DESC);
    oprot.writeI32(this.iSequence);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(I_VISIBLE_FIELD_DESC);
    oprot.writeBool(this.iVisible);
    oprot.writeFieldEnd();
    if (this.iTextContent != null) {
      oprot.writeFieldBegin(I_TEXT_CONTENT_FIELD_DESC);
      oprot.writeString(this.iTextContent);
      oprot.writeFieldEnd();
    }
    if (this.iVideoSourceProvider != null) {
      oprot.writeFieldBegin(I_VIDEO_SOURCE_PROVIDER_FIELD_DESC);
      oprot.writeString(this.iVideoSourceProvider);
      oprot.writeFieldEnd();
    }
    if (this.iVideoURL != null) {
      oprot.writeFieldBegin(I_VIDEO_URL_FIELD_DESC);
      oprot.writeString(this.iVideoURL);
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
    StringBuilder sb = new StringBuilder("MyEduModule(");
    boolean first = true;

    sb.append("iId:");
    sb.append(this.iId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iSectionId:");
    sb.append(this.iSectionId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iSequence:");
    sb.append(this.iSequence);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iVisible:");
    sb.append(this.iVisible);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iTextContent:");
    if (this.iTextContent == null) {
      sb.append("null");
    } else {
      sb.append(this.iTextContent);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("iVideoSourceProvider:");
    if (this.iVideoSourceProvider == null) {
      sb.append("null");
    } else {
      sb.append(this.iVideoSourceProvider);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("iVideoURL:");
    if (this.iVideoURL == null) {
      sb.append("null");
    } else {
      sb.append(this.iVideoURL);
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
    // alas, we cannot check 'iSectionId' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'iSequence' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'iVisible' because it's a primitive and you chose the non-beans generator.
    if (iTextContent == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iTextContent' was not present! Struct: " + toString());
    }
    if (iVideoSourceProvider == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iVideoSourceProvider' was not present! Struct: " + toString());
    }
    if (iVideoURL == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iVideoURL' was not present! Struct: " + toString());
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

