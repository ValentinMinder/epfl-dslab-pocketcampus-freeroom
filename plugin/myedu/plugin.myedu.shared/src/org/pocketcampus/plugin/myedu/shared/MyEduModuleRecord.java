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

public class MyEduModuleRecord implements org.apache.thrift.TBase<MyEduModuleRecord, MyEduModuleRecord._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MyEduModuleRecord");

  private static final org.apache.thrift.protocol.TField I_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("iId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField I_MODULE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("iModuleId", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField I_FEEDBACK_TEXT_FIELD_DESC = new org.apache.thrift.protocol.TField("iFeedbackText", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField I_FEEDBACK_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("iFeedbackTimestamp", org.apache.thrift.protocol.TType.I64, (short)4);
  private static final org.apache.thrift.protocol.TField I_MODULE_COMPLETED_FIELD_DESC = new org.apache.thrift.protocol.TField("iModuleCompleted", org.apache.thrift.protocol.TType.BOOL, (short)5);
  private static final org.apache.thrift.protocol.TField I_RATING_FIELD_DESC = new org.apache.thrift.protocol.TField("iRating", org.apache.thrift.protocol.TType.I32, (short)6);
  private static final org.apache.thrift.protocol.TField I_USER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("iUserId", org.apache.thrift.protocol.TType.I32, (short)7);
  private static final org.apache.thrift.protocol.TField I_CREATION_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("iCreationTimestamp", org.apache.thrift.protocol.TType.I64, (short)8);
  private static final org.apache.thrift.protocol.TField I_LAST_UPDATE_TIMESTAMP_FIELD_DESC = new org.apache.thrift.protocol.TField("iLastUpdateTimestamp", org.apache.thrift.protocol.TType.I64, (short)9);

  public int iId; // required
  public int iModuleId; // required
  public String iFeedbackText; // required
  public long iFeedbackTimestamp; // required
  public boolean iModuleCompleted; // required
  public int iRating; // required
  public int iUserId; // required
  public long iCreationTimestamp; // required
  public long iLastUpdateTimestamp; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    I_ID((short)1, "iId"),
    I_MODULE_ID((short)2, "iModuleId"),
    I_FEEDBACK_TEXT((short)3, "iFeedbackText"),
    I_FEEDBACK_TIMESTAMP((short)4, "iFeedbackTimestamp"),
    I_MODULE_COMPLETED((short)5, "iModuleCompleted"),
    I_RATING((short)6, "iRating"),
    I_USER_ID((short)7, "iUserId"),
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
        case 2: // I_MODULE_ID
          return I_MODULE_ID;
        case 3: // I_FEEDBACK_TEXT
          return I_FEEDBACK_TEXT;
        case 4: // I_FEEDBACK_TIMESTAMP
          return I_FEEDBACK_TIMESTAMP;
        case 5: // I_MODULE_COMPLETED
          return I_MODULE_COMPLETED;
        case 6: // I_RATING
          return I_RATING;
        case 7: // I_USER_ID
          return I_USER_ID;
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
  private static final int __IMODULEID_ISSET_ID = 1;
  private static final int __IFEEDBACKTIMESTAMP_ISSET_ID = 2;
  private static final int __IMODULECOMPLETED_ISSET_ID = 3;
  private static final int __IRATING_ISSET_ID = 4;
  private static final int __IUSERID_ISSET_ID = 5;
  private static final int __ICREATIONTIMESTAMP_ISSET_ID = 6;
  private static final int __ILASTUPDATETIMESTAMP_ISSET_ID = 7;
  private BitSet __isset_bit_vector = new BitSet(8);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.I_ID, new org.apache.thrift.meta_data.FieldMetaData("iId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_MODULE_ID, new org.apache.thrift.meta_data.FieldMetaData("iModuleId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_FEEDBACK_TEXT, new org.apache.thrift.meta_data.FieldMetaData("iFeedbackText", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.I_FEEDBACK_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("iFeedbackTimestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "timestamp")));
    tmpMap.put(_Fields.I_MODULE_COMPLETED, new org.apache.thrift.meta_data.FieldMetaData("iModuleCompleted", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    tmpMap.put(_Fields.I_RATING, new org.apache.thrift.meta_data.FieldMetaData("iRating", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_USER_ID, new org.apache.thrift.meta_data.FieldMetaData("iUserId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.I_CREATION_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("iCreationTimestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "timestamp")));
    tmpMap.put(_Fields.I_LAST_UPDATE_TIMESTAMP, new org.apache.thrift.meta_data.FieldMetaData("iLastUpdateTimestamp", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "timestamp")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MyEduModuleRecord.class, metaDataMap);
  }

  public MyEduModuleRecord() {
  }

  public MyEduModuleRecord(
    int iId,
    int iModuleId,
    String iFeedbackText,
    long iFeedbackTimestamp,
    boolean iModuleCompleted,
    int iRating,
    int iUserId,
    long iCreationTimestamp,
    long iLastUpdateTimestamp)
  {
    this();
    this.iId = iId;
    setIIdIsSet(true);
    this.iModuleId = iModuleId;
    setIModuleIdIsSet(true);
    this.iFeedbackText = iFeedbackText;
    this.iFeedbackTimestamp = iFeedbackTimestamp;
    setIFeedbackTimestampIsSet(true);
    this.iModuleCompleted = iModuleCompleted;
    setIModuleCompletedIsSet(true);
    this.iRating = iRating;
    setIRatingIsSet(true);
    this.iUserId = iUserId;
    setIUserIdIsSet(true);
    this.iCreationTimestamp = iCreationTimestamp;
    setICreationTimestampIsSet(true);
    this.iLastUpdateTimestamp = iLastUpdateTimestamp;
    setILastUpdateTimestampIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MyEduModuleRecord(MyEduModuleRecord other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.iId = other.iId;
    this.iModuleId = other.iModuleId;
    if (other.isSetIFeedbackText()) {
      this.iFeedbackText = other.iFeedbackText;
    }
    this.iFeedbackTimestamp = other.iFeedbackTimestamp;
    this.iModuleCompleted = other.iModuleCompleted;
    this.iRating = other.iRating;
    this.iUserId = other.iUserId;
    this.iCreationTimestamp = other.iCreationTimestamp;
    this.iLastUpdateTimestamp = other.iLastUpdateTimestamp;
  }

  public MyEduModuleRecord deepCopy() {
    return new MyEduModuleRecord(this);
  }

  @Override
  public void clear() {
    setIIdIsSet(false);
    this.iId = 0;
    setIModuleIdIsSet(false);
    this.iModuleId = 0;
    this.iFeedbackText = null;
    setIFeedbackTimestampIsSet(false);
    this.iFeedbackTimestamp = 0;
    setIModuleCompletedIsSet(false);
    this.iModuleCompleted = false;
    setIRatingIsSet(false);
    this.iRating = 0;
    setIUserIdIsSet(false);
    this.iUserId = 0;
    setICreationTimestampIsSet(false);
    this.iCreationTimestamp = 0;
    setILastUpdateTimestampIsSet(false);
    this.iLastUpdateTimestamp = 0;
  }

  public int getIId() {
    return this.iId;
  }

  public MyEduModuleRecord setIId(int iId) {
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

  public MyEduModuleRecord setIModuleId(int iModuleId) {
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

  public String getIFeedbackText() {
    return this.iFeedbackText;
  }

  public MyEduModuleRecord setIFeedbackText(String iFeedbackText) {
    this.iFeedbackText = iFeedbackText;
    return this;
  }

  public void unsetIFeedbackText() {
    this.iFeedbackText = null;
  }

  /** Returns true if field iFeedbackText is set (has been assigned a value) and false otherwise */
  public boolean isSetIFeedbackText() {
    return this.iFeedbackText != null;
  }

  public void setIFeedbackTextIsSet(boolean value) {
    if (!value) {
      this.iFeedbackText = null;
    }
  }

  public long getIFeedbackTimestamp() {
    return this.iFeedbackTimestamp;
  }

  public MyEduModuleRecord setIFeedbackTimestamp(long iFeedbackTimestamp) {
    this.iFeedbackTimestamp = iFeedbackTimestamp;
    setIFeedbackTimestampIsSet(true);
    return this;
  }

  public void unsetIFeedbackTimestamp() {
    __isset_bit_vector.clear(__IFEEDBACKTIMESTAMP_ISSET_ID);
  }

  /** Returns true if field iFeedbackTimestamp is set (has been assigned a value) and false otherwise */
  public boolean isSetIFeedbackTimestamp() {
    return __isset_bit_vector.get(__IFEEDBACKTIMESTAMP_ISSET_ID);
  }

  public void setIFeedbackTimestampIsSet(boolean value) {
    __isset_bit_vector.set(__IFEEDBACKTIMESTAMP_ISSET_ID, value);
  }

  public boolean isIModuleCompleted() {
    return this.iModuleCompleted;
  }

  public MyEduModuleRecord setIModuleCompleted(boolean iModuleCompleted) {
    this.iModuleCompleted = iModuleCompleted;
    setIModuleCompletedIsSet(true);
    return this;
  }

  public void unsetIModuleCompleted() {
    __isset_bit_vector.clear(__IMODULECOMPLETED_ISSET_ID);
  }

  /** Returns true if field iModuleCompleted is set (has been assigned a value) and false otherwise */
  public boolean isSetIModuleCompleted() {
    return __isset_bit_vector.get(__IMODULECOMPLETED_ISSET_ID);
  }

  public void setIModuleCompletedIsSet(boolean value) {
    __isset_bit_vector.set(__IMODULECOMPLETED_ISSET_ID, value);
  }

  public int getIRating() {
    return this.iRating;
  }

  public MyEduModuleRecord setIRating(int iRating) {
    this.iRating = iRating;
    setIRatingIsSet(true);
    return this;
  }

  public void unsetIRating() {
    __isset_bit_vector.clear(__IRATING_ISSET_ID);
  }

  /** Returns true if field iRating is set (has been assigned a value) and false otherwise */
  public boolean isSetIRating() {
    return __isset_bit_vector.get(__IRATING_ISSET_ID);
  }

  public void setIRatingIsSet(boolean value) {
    __isset_bit_vector.set(__IRATING_ISSET_ID, value);
  }

  public int getIUserId() {
    return this.iUserId;
  }

  public MyEduModuleRecord setIUserId(int iUserId) {
    this.iUserId = iUserId;
    setIUserIdIsSet(true);
    return this;
  }

  public void unsetIUserId() {
    __isset_bit_vector.clear(__IUSERID_ISSET_ID);
  }

  /** Returns true if field iUserId is set (has been assigned a value) and false otherwise */
  public boolean isSetIUserId() {
    return __isset_bit_vector.get(__IUSERID_ISSET_ID);
  }

  public void setIUserIdIsSet(boolean value) {
    __isset_bit_vector.set(__IUSERID_ISSET_ID, value);
  }

  public long getICreationTimestamp() {
    return this.iCreationTimestamp;
  }

  public MyEduModuleRecord setICreationTimestamp(long iCreationTimestamp) {
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

  public MyEduModuleRecord setILastUpdateTimestamp(long iLastUpdateTimestamp) {
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

    case I_FEEDBACK_TEXT:
      if (value == null) {
        unsetIFeedbackText();
      } else {
        setIFeedbackText((String)value);
      }
      break;

    case I_FEEDBACK_TIMESTAMP:
      if (value == null) {
        unsetIFeedbackTimestamp();
      } else {
        setIFeedbackTimestamp((Long)value);
      }
      break;

    case I_MODULE_COMPLETED:
      if (value == null) {
        unsetIModuleCompleted();
      } else {
        setIModuleCompleted((Boolean)value);
      }
      break;

    case I_RATING:
      if (value == null) {
        unsetIRating();
      } else {
        setIRating((Integer)value);
      }
      break;

    case I_USER_ID:
      if (value == null) {
        unsetIUserId();
      } else {
        setIUserId((Integer)value);
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

    case I_FEEDBACK_TEXT:
      return getIFeedbackText();

    case I_FEEDBACK_TIMESTAMP:
      return Long.valueOf(getIFeedbackTimestamp());

    case I_MODULE_COMPLETED:
      return Boolean.valueOf(isIModuleCompleted());

    case I_RATING:
      return Integer.valueOf(getIRating());

    case I_USER_ID:
      return Integer.valueOf(getIUserId());

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
    case I_FEEDBACK_TEXT:
      return isSetIFeedbackText();
    case I_FEEDBACK_TIMESTAMP:
      return isSetIFeedbackTimestamp();
    case I_MODULE_COMPLETED:
      return isSetIModuleCompleted();
    case I_RATING:
      return isSetIRating();
    case I_USER_ID:
      return isSetIUserId();
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
    if (that instanceof MyEduModuleRecord)
      return this.equals((MyEduModuleRecord)that);
    return false;
  }

  public boolean equals(MyEduModuleRecord that) {
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

    boolean this_present_iFeedbackText = true && this.isSetIFeedbackText();
    boolean that_present_iFeedbackText = true && that.isSetIFeedbackText();
    if (this_present_iFeedbackText || that_present_iFeedbackText) {
      if (!(this_present_iFeedbackText && that_present_iFeedbackText))
        return false;
      if (!this.iFeedbackText.equals(that.iFeedbackText))
        return false;
    }

    boolean this_present_iFeedbackTimestamp = true;
    boolean that_present_iFeedbackTimestamp = true;
    if (this_present_iFeedbackTimestamp || that_present_iFeedbackTimestamp) {
      if (!(this_present_iFeedbackTimestamp && that_present_iFeedbackTimestamp))
        return false;
      if (this.iFeedbackTimestamp != that.iFeedbackTimestamp)
        return false;
    }

    boolean this_present_iModuleCompleted = true;
    boolean that_present_iModuleCompleted = true;
    if (this_present_iModuleCompleted || that_present_iModuleCompleted) {
      if (!(this_present_iModuleCompleted && that_present_iModuleCompleted))
        return false;
      if (this.iModuleCompleted != that.iModuleCompleted)
        return false;
    }

    boolean this_present_iRating = true;
    boolean that_present_iRating = true;
    if (this_present_iRating || that_present_iRating) {
      if (!(this_present_iRating && that_present_iRating))
        return false;
      if (this.iRating != that.iRating)
        return false;
    }

    boolean this_present_iUserId = true;
    boolean that_present_iUserId = true;
    if (this_present_iUserId || that_present_iUserId) {
      if (!(this_present_iUserId && that_present_iUserId))
        return false;
      if (this.iUserId != that.iUserId)
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

    boolean present_iFeedbackText = true && (isSetIFeedbackText());
    builder.append(present_iFeedbackText);
    if (present_iFeedbackText)
      builder.append(iFeedbackText);

    boolean present_iFeedbackTimestamp = true;
    builder.append(present_iFeedbackTimestamp);
    if (present_iFeedbackTimestamp)
      builder.append(iFeedbackTimestamp);

    boolean present_iModuleCompleted = true;
    builder.append(present_iModuleCompleted);
    if (present_iModuleCompleted)
      builder.append(iModuleCompleted);

    boolean present_iRating = true;
    builder.append(present_iRating);
    if (present_iRating)
      builder.append(iRating);

    boolean present_iUserId = true;
    builder.append(present_iUserId);
    if (present_iUserId)
      builder.append(iUserId);

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

  public int compareTo(MyEduModuleRecord other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    MyEduModuleRecord typedOther = (MyEduModuleRecord)other;

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
    lastComparison = Boolean.valueOf(isSetIFeedbackText()).compareTo(typedOther.isSetIFeedbackText());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIFeedbackText()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iFeedbackText, typedOther.iFeedbackText);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIFeedbackTimestamp()).compareTo(typedOther.isSetIFeedbackTimestamp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIFeedbackTimestamp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iFeedbackTimestamp, typedOther.iFeedbackTimestamp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIModuleCompleted()).compareTo(typedOther.isSetIModuleCompleted());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIModuleCompleted()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iModuleCompleted, typedOther.iModuleCompleted);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIRating()).compareTo(typedOther.isSetIRating());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIRating()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iRating, typedOther.iRating);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIUserId()).compareTo(typedOther.isSetIUserId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIUserId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.iUserId, typedOther.iUserId);
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
        case 3: // I_FEEDBACK_TEXT
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.iFeedbackText = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // I_FEEDBACK_TIMESTAMP
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.iFeedbackTimestamp = iprot.readI64();
            setIFeedbackTimestampIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // I_MODULE_COMPLETED
          if (field.type == org.apache.thrift.protocol.TType.BOOL) {
            this.iModuleCompleted = iprot.readBool();
            setIModuleCompletedIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 6: // I_RATING
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.iRating = iprot.readI32();
            setIRatingIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 7: // I_USER_ID
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.iUserId = iprot.readI32();
            setIUserIdIsSet(true);
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
    if (!isSetIModuleId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iModuleId' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetIFeedbackTimestamp()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iFeedbackTimestamp' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetIModuleCompleted()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iModuleCompleted' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetIRating()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iRating' was not found in serialized data! Struct: " + toString());
    }
    if (!isSetIUserId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iUserId' was not found in serialized data! Struct: " + toString());
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
    if (this.iFeedbackText != null) {
      oprot.writeFieldBegin(I_FEEDBACK_TEXT_FIELD_DESC);
      oprot.writeString(this.iFeedbackText);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(I_FEEDBACK_TIMESTAMP_FIELD_DESC);
    oprot.writeI64(this.iFeedbackTimestamp);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(I_MODULE_COMPLETED_FIELD_DESC);
    oprot.writeBool(this.iModuleCompleted);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(I_RATING_FIELD_DESC);
    oprot.writeI32(this.iRating);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(I_USER_ID_FIELD_DESC);
    oprot.writeI32(this.iUserId);
    oprot.writeFieldEnd();
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
    StringBuilder sb = new StringBuilder("MyEduModuleRecord(");
    boolean first = true;

    sb.append("iId:");
    sb.append(this.iId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iModuleId:");
    sb.append(this.iModuleId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iFeedbackText:");
    if (this.iFeedbackText == null) {
      sb.append("null");
    } else {
      sb.append(this.iFeedbackText);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("iFeedbackTimestamp:");
    sb.append(this.iFeedbackTimestamp);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iModuleCompleted:");
    sb.append(this.iModuleCompleted);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iRating:");
    sb.append(this.iRating);
    first = false;
    if (!first) sb.append(", ");
    sb.append("iUserId:");
    sb.append(this.iUserId);
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
    if (iFeedbackText == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'iFeedbackText' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'iFeedbackTimestamp' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'iModuleCompleted' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'iRating' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'iUserId' because it's a primitive and you chose the non-beans generator.
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

