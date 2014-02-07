/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.edx.shared;

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

public class EdxItemVideo implements org.apache.thrift.TBase<EdxItemVideo, EdxItemVideo._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EdxItemVideo");

  private static final org.apache.thrift.protocol.TField ITEM_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("itemId", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField YOUTUBE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("youtubeId", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField TITLE_FIELD_DESC = new org.apache.thrift.protocol.TField("title", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField HTML_FIELD_DESC = new org.apache.thrift.protocol.TField("html", org.apache.thrift.protocol.TType.STRING, (short)4);

  private String itemId; // required
  private String youtubeId; // required
  private String title; // required
  private String html; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ITEM_ID((short)1, "itemId"),
    YOUTUBE_ID((short)2, "youtubeId"),
    TITLE((short)3, "title"),
    HTML((short)4, "html");

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
        case 1: // ITEM_ID
          return ITEM_ID;
        case 2: // YOUTUBE_ID
          return YOUTUBE_ID;
        case 3: // TITLE
          return TITLE;
        case 4: // HTML
          return HTML;
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
    tmpMap.put(_Fields.ITEM_ID, new org.apache.thrift.meta_data.FieldMetaData("itemId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.YOUTUBE_ID, new org.apache.thrift.meta_data.FieldMetaData("youtubeId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TITLE, new org.apache.thrift.meta_data.FieldMetaData("title", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.HTML, new org.apache.thrift.meta_data.FieldMetaData("html", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EdxItemVideo.class, metaDataMap);
  }

  public EdxItemVideo() {
  }

  public EdxItemVideo(
    String itemId,
    String youtubeId,
    String title)
  {
    this();
    this.itemId = itemId;
    this.youtubeId = youtubeId;
    this.title = title;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EdxItemVideo(EdxItemVideo other) {
    if (other.isSetItemId()) {
      this.itemId = other.itemId;
    }
    if (other.isSetYoutubeId()) {
      this.youtubeId = other.youtubeId;
    }
    if (other.isSetTitle()) {
      this.title = other.title;
    }
    if (other.isSetHtml()) {
      this.html = other.html;
    }
  }

  public EdxItemVideo deepCopy() {
    return new EdxItemVideo(this);
  }

  @Override
  public void clear() {
    this.itemId = null;
    this.youtubeId = null;
    this.title = null;
    this.html = null;
  }

  public String getItemId() {
    return this.itemId;
  }

  public EdxItemVideo setItemId(String itemId) {
    this.itemId = itemId;
    return this;
  }

  public void unsetItemId() {
    this.itemId = null;
  }

  /** Returns true if field itemId is set (has been assigned a value) and false otherwise */
  public boolean isSetItemId() {
    return this.itemId != null;
  }

  public void setItemIdIsSet(boolean value) {
    if (!value) {
      this.itemId = null;
    }
  }

  public String getYoutubeId() {
    return this.youtubeId;
  }

  public EdxItemVideo setYoutubeId(String youtubeId) {
    this.youtubeId = youtubeId;
    return this;
  }

  public void unsetYoutubeId() {
    this.youtubeId = null;
  }

  /** Returns true if field youtubeId is set (has been assigned a value) and false otherwise */
  public boolean isSetYoutubeId() {
    return this.youtubeId != null;
  }

  public void setYoutubeIdIsSet(boolean value) {
    if (!value) {
      this.youtubeId = null;
    }
  }

  public String getTitle() {
    return this.title;
  }

  public EdxItemVideo setTitle(String title) {
    this.title = title;
    return this;
  }

  public void unsetTitle() {
    this.title = null;
  }

  /** Returns true if field title is set (has been assigned a value) and false otherwise */
  public boolean isSetTitle() {
    return this.title != null;
  }

  public void setTitleIsSet(boolean value) {
    if (!value) {
      this.title = null;
    }
  }

  public String getHtml() {
    return this.html;
  }

  public EdxItemVideo setHtml(String html) {
    this.html = html;
    return this;
  }

  public void unsetHtml() {
    this.html = null;
  }

  /** Returns true if field html is set (has been assigned a value) and false otherwise */
  public boolean isSetHtml() {
    return this.html != null;
  }

  public void setHtmlIsSet(boolean value) {
    if (!value) {
      this.html = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ITEM_ID:
      if (value == null) {
        unsetItemId();
      } else {
        setItemId((String)value);
      }
      break;

    case YOUTUBE_ID:
      if (value == null) {
        unsetYoutubeId();
      } else {
        setYoutubeId((String)value);
      }
      break;

    case TITLE:
      if (value == null) {
        unsetTitle();
      } else {
        setTitle((String)value);
      }
      break;

    case HTML:
      if (value == null) {
        unsetHtml();
      } else {
        setHtml((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ITEM_ID:
      return getItemId();

    case YOUTUBE_ID:
      return getYoutubeId();

    case TITLE:
      return getTitle();

    case HTML:
      return getHtml();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ITEM_ID:
      return isSetItemId();
    case YOUTUBE_ID:
      return isSetYoutubeId();
    case TITLE:
      return isSetTitle();
    case HTML:
      return isSetHtml();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof EdxItemVideo)
      return this.equals((EdxItemVideo)that);
    return false;
  }

  public boolean equals(EdxItemVideo that) {
    if (that == null)
      return false;

    boolean this_present_itemId = true && this.isSetItemId();
    boolean that_present_itemId = true && that.isSetItemId();
    if (this_present_itemId || that_present_itemId) {
      if (!(this_present_itemId && that_present_itemId))
        return false;
      if (!this.itemId.equals(that.itemId))
        return false;
    }

    boolean this_present_youtubeId = true && this.isSetYoutubeId();
    boolean that_present_youtubeId = true && that.isSetYoutubeId();
    if (this_present_youtubeId || that_present_youtubeId) {
      if (!(this_present_youtubeId && that_present_youtubeId))
        return false;
      if (!this.youtubeId.equals(that.youtubeId))
        return false;
    }

    boolean this_present_title = true && this.isSetTitle();
    boolean that_present_title = true && that.isSetTitle();
    if (this_present_title || that_present_title) {
      if (!(this_present_title && that_present_title))
        return false;
      if (!this.title.equals(that.title))
        return false;
    }

    boolean this_present_html = true && this.isSetHtml();
    boolean that_present_html = true && that.isSetHtml();
    if (this_present_html || that_present_html) {
      if (!(this_present_html && that_present_html))
        return false;
      if (!this.html.equals(that.html))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_itemId = true && (isSetItemId());
    builder.append(present_itemId);
    if (present_itemId)
      builder.append(itemId);

    boolean present_youtubeId = true && (isSetYoutubeId());
    builder.append(present_youtubeId);
    if (present_youtubeId)
      builder.append(youtubeId);

    boolean present_title = true && (isSetTitle());
    builder.append(present_title);
    if (present_title)
      builder.append(title);

    boolean present_html = true && (isSetHtml());
    builder.append(present_html);
    if (present_html)
      builder.append(html);

    return builder.toHashCode();
  }

  public int compareTo(EdxItemVideo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    EdxItemVideo typedOther = (EdxItemVideo)other;

    lastComparison = Boolean.valueOf(isSetItemId()).compareTo(typedOther.isSetItemId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetItemId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.itemId, typedOther.itemId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetYoutubeId()).compareTo(typedOther.isSetYoutubeId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetYoutubeId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.youtubeId, typedOther.youtubeId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTitle()).compareTo(typedOther.isSetTitle());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTitle()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.title, typedOther.title);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetHtml()).compareTo(typedOther.isSetHtml());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHtml()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.html, typedOther.html);
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
        case 1: // ITEM_ID
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.itemId = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // YOUTUBE_ID
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.youtubeId = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // TITLE
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.title = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // HTML
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.html = iprot.readString();
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
    if (this.itemId != null) {
      oprot.writeFieldBegin(ITEM_ID_FIELD_DESC);
      oprot.writeString(this.itemId);
      oprot.writeFieldEnd();
    }
    if (this.youtubeId != null) {
      oprot.writeFieldBegin(YOUTUBE_ID_FIELD_DESC);
      oprot.writeString(this.youtubeId);
      oprot.writeFieldEnd();
    }
    if (this.title != null) {
      oprot.writeFieldBegin(TITLE_FIELD_DESC);
      oprot.writeString(this.title);
      oprot.writeFieldEnd();
    }
    if (this.html != null) {
      if (isSetHtml()) {
        oprot.writeFieldBegin(HTML_FIELD_DESC);
        oprot.writeString(this.html);
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("EdxItemVideo(");
    boolean first = true;

    sb.append("itemId:");
    if (this.itemId == null) {
      sb.append("null");
    } else {
      sb.append(this.itemId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("youtubeId:");
    if (this.youtubeId == null) {
      sb.append("null");
    } else {
      sb.append(this.youtubeId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("title:");
    if (this.title == null) {
      sb.append("null");
    } else {
      sb.append(this.title);
    }
    first = false;
    if (isSetHtml()) {
      if (!first) sb.append(", ");
      sb.append("html:");
      if (this.html == null) {
        sb.append("null");
      } else {
        sb.append(this.html);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (itemId == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'itemId' was not present! Struct: " + toString());
    }
    if (youtubeId == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'youtubeId' was not present! Struct: " + toString());
    }
    if (title == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'title' was not present! Struct: " + toString());
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

