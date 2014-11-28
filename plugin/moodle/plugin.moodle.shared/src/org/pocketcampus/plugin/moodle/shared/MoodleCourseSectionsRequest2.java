/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.pocketcampus.plugin.moodle.shared;

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
public class MoodleCourseSectionsRequest2 implements org.apache.thrift.TBase<MoodleCourseSectionsRequest2, MoodleCourseSectionsRequest2._Fields>, java.io.Serializable, Cloneable, Comparable<MoodleCourseSectionsRequest2> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MoodleCourseSectionsRequest2");

  private static final org.apache.thrift.protocol.TField LANGUAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("language", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField COURSE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("courseId", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new MoodleCourseSectionsRequest2StandardSchemeFactory());
    schemes.put(TupleScheme.class, new MoodleCourseSectionsRequest2TupleSchemeFactory());
  }

  private String language; // required
  private int courseId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    LANGUAGE((short)1, "language"),
    COURSE_ID((short)2, "courseId");

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
        case 1: // LANGUAGE
          return LANGUAGE;
        case 2: // COURSE_ID
          return COURSE_ID;
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
  private static final int __COURSEID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.LANGUAGE, new org.apache.thrift.meta_data.FieldMetaData("language", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.COURSE_ID, new org.apache.thrift.meta_data.FieldMetaData("courseId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MoodleCourseSectionsRequest2.class, metaDataMap);
  }

  public MoodleCourseSectionsRequest2() {
  }

  public MoodleCourseSectionsRequest2(
    String language,
    int courseId)
  {
    this();
    this.language = language;
    this.courseId = courseId;
    setCourseIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MoodleCourseSectionsRequest2(MoodleCourseSectionsRequest2 other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetLanguage()) {
      this.language = other.language;
    }
    this.courseId = other.courseId;
  }

  public MoodleCourseSectionsRequest2 deepCopy() {
    return new MoodleCourseSectionsRequest2(this);
  }

  @Override
  public void clear() {
    this.language = null;
    setCourseIdIsSet(false);
    this.courseId = 0;
  }

  public String getLanguage() {
    return this.language;
  }

  public MoodleCourseSectionsRequest2 setLanguage(String language) {
    this.language = language;
    return this;
  }

  public void unsetLanguage() {
    this.language = null;
  }

  /** Returns true if field language is set (has been assigned a value) and false otherwise */
  public boolean isSetLanguage() {
    return this.language != null;
  }

  public void setLanguageIsSet(boolean value) {
    if (!value) {
      this.language = null;
    }
  }

  public int getCourseId() {
    return this.courseId;
  }

  public MoodleCourseSectionsRequest2 setCourseId(int courseId) {
    this.courseId = courseId;
    setCourseIdIsSet(true);
    return this;
  }

  public void unsetCourseId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __COURSEID_ISSET_ID);
  }

  /** Returns true if field courseId is set (has been assigned a value) and false otherwise */
  public boolean isSetCourseId() {
    return EncodingUtils.testBit(__isset_bitfield, __COURSEID_ISSET_ID);
  }

  public void setCourseIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __COURSEID_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case LANGUAGE:
      if (value == null) {
        unsetLanguage();
      } else {
        setLanguage((String)value);
      }
      break;

    case COURSE_ID:
      if (value == null) {
        unsetCourseId();
      } else {
        setCourseId((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case LANGUAGE:
      return getLanguage();

    case COURSE_ID:
      return Integer.valueOf(getCourseId());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case LANGUAGE:
      return isSetLanguage();
    case COURSE_ID:
      return isSetCourseId();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MoodleCourseSectionsRequest2)
      return this.equals((MoodleCourseSectionsRequest2)that);
    return false;
  }

  public boolean equals(MoodleCourseSectionsRequest2 that) {
    if (that == null)
      return false;

    boolean this_present_language = true && this.isSetLanguage();
    boolean that_present_language = true && that.isSetLanguage();
    if (this_present_language || that_present_language) {
      if (!(this_present_language && that_present_language))
        return false;
      if (!this.language.equals(that.language))
        return false;
    }

    boolean this_present_courseId = true;
    boolean that_present_courseId = true;
    if (this_present_courseId || that_present_courseId) {
      if (!(this_present_courseId && that_present_courseId))
        return false;
      if (this.courseId != that.courseId)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_language = true && (isSetLanguage());
    list.add(present_language);
    if (present_language)
      list.add(language);

    boolean present_courseId = true;
    list.add(present_courseId);
    if (present_courseId)
      list.add(courseId);

    return list.hashCode();
  }

  @Override
  public int compareTo(MoodleCourseSectionsRequest2 other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetLanguage()).compareTo(other.isSetLanguage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLanguage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.language, other.language);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCourseId()).compareTo(other.isSetCourseId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCourseId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.courseId, other.courseId);
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
    StringBuilder sb = new StringBuilder("MoodleCourseSectionsRequest2(");
    boolean first = true;

    sb.append("language:");
    if (this.language == null) {
      sb.append("null");
    } else {
      sb.append(this.language);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("courseId:");
    sb.append(this.courseId);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (language == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'language' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'courseId' because it's a primitive and you chose the non-beans generator.
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

  private static class MoodleCourseSectionsRequest2StandardSchemeFactory implements SchemeFactory {
    public MoodleCourseSectionsRequest2StandardScheme getScheme() {
      return new MoodleCourseSectionsRequest2StandardScheme();
    }
  }

  private static class MoodleCourseSectionsRequest2StandardScheme extends StandardScheme<MoodleCourseSectionsRequest2> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MoodleCourseSectionsRequest2 struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // LANGUAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.language = iprot.readString();
              struct.setLanguageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // COURSE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.courseId = iprot.readI32();
              struct.setCourseIdIsSet(true);
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
      if (!struct.isSetCourseId()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'courseId' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, MoodleCourseSectionsRequest2 struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.language != null) {
        oprot.writeFieldBegin(LANGUAGE_FIELD_DESC);
        oprot.writeString(struct.language);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(COURSE_ID_FIELD_DESC);
      oprot.writeI32(struct.courseId);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MoodleCourseSectionsRequest2TupleSchemeFactory implements SchemeFactory {
    public MoodleCourseSectionsRequest2TupleScheme getScheme() {
      return new MoodleCourseSectionsRequest2TupleScheme();
    }
  }

  private static class MoodleCourseSectionsRequest2TupleScheme extends TupleScheme<MoodleCourseSectionsRequest2> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MoodleCourseSectionsRequest2 struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.language);
      oprot.writeI32(struct.courseId);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MoodleCourseSectionsRequest2 struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.language = iprot.readString();
      struct.setLanguageIsSet(true);
      struct.courseId = iprot.readI32();
      struct.setCourseIdIsSet(true);
    }
  }

}

