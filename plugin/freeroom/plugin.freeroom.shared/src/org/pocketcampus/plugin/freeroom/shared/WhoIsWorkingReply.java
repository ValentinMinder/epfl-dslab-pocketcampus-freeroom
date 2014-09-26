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

public class WhoIsWorkingReply implements org.apache.thrift.TBase<WhoIsWorkingReply, WhoIsWorkingReply._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("WhoIsWorkingReply");

  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField STATUS_COMMENT_FIELD_DESC = new org.apache.thrift.protocol.TField("statusComment", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField MESSAGES_FIELD_DESC = new org.apache.thrift.protocol.TField("messages", org.apache.thrift.protocol.TType.LIST, (short)3);

  private FRStatusCode status; // required
  private String statusComment; // required
  private List<MessageFrequency> messages; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see FRStatusCode
     */
    STATUS((short)1, "status"),
    STATUS_COMMENT((short)2, "statusComment"),
    MESSAGES((short)3, "messages");

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
        case 2: // STATUS_COMMENT
          return STATUS_COMMENT;
        case 3: // MESSAGES
          return MESSAGES;
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
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, FRStatusCode.class)));
    tmpMap.put(_Fields.STATUS_COMMENT, new org.apache.thrift.meta_data.FieldMetaData("statusComment", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.MESSAGES, new org.apache.thrift.meta_data.FieldMetaData("messages", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, MessageFrequency.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(WhoIsWorkingReply.class, metaDataMap);
  }

  public WhoIsWorkingReply() {
  }

  public WhoIsWorkingReply(
    FRStatusCode status,
    String statusComment)
  {
    this();
    this.status = status;
    this.statusComment = statusComment;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public WhoIsWorkingReply(WhoIsWorkingReply other) {
    if (other.isSetStatus()) {
      this.status = other.status;
    }
    if (other.isSetStatusComment()) {
      this.statusComment = other.statusComment;
    }
    if (other.isSetMessages()) {
      List<MessageFrequency> __this__messages = new ArrayList<MessageFrequency>();
      for (MessageFrequency other_element : other.messages) {
        __this__messages.add(new MessageFrequency(other_element));
      }
      this.messages = __this__messages;
    }
  }

  public WhoIsWorkingReply deepCopy() {
    return new WhoIsWorkingReply(this);
  }

  @Override
  public void clear() {
    this.status = null;
    this.statusComment = null;
    this.messages = null;
  }

  /**
   * 
   * @see FRStatusCode
   */
  public FRStatusCode getStatus() {
    return this.status;
  }

  /**
   * 
   * @see FRStatusCode
   */
  public WhoIsWorkingReply setStatus(FRStatusCode status) {
    this.status = status;
    return this;
  }

  public void unsetStatus() {
    this.status = null;
  }

  /** Returns true if field status is set (has been assigned a value) and false otherwise */
  public boolean isSetStatus() {
    return this.status != null;
  }

  public void setStatusIsSet(boolean value) {
    if (!value) {
      this.status = null;
    }
  }

  public String getStatusComment() {
    return this.statusComment;
  }

  public WhoIsWorkingReply setStatusComment(String statusComment) {
    this.statusComment = statusComment;
    return this;
  }

  public void unsetStatusComment() {
    this.statusComment = null;
  }

  /** Returns true if field statusComment is set (has been assigned a value) and false otherwise */
  public boolean isSetStatusComment() {
    return this.statusComment != null;
  }

  public void setStatusCommentIsSet(boolean value) {
    if (!value) {
      this.statusComment = null;
    }
  }

  public int getMessagesSize() {
    return (this.messages == null) ? 0 : this.messages.size();
  }

  public java.util.Iterator<MessageFrequency> getMessagesIterator() {
    return (this.messages == null) ? null : this.messages.iterator();
  }

  public void addToMessages(MessageFrequency elem) {
    if (this.messages == null) {
      this.messages = new ArrayList<MessageFrequency>();
    }
    this.messages.add(elem);
  }

  public List<MessageFrequency> getMessages() {
    return this.messages;
  }

  public WhoIsWorkingReply setMessages(List<MessageFrequency> messages) {
    this.messages = messages;
    return this;
  }

  public void unsetMessages() {
    this.messages = null;
  }

  /** Returns true if field messages is set (has been assigned a value) and false otherwise */
  public boolean isSetMessages() {
    return this.messages != null;
  }

  public void setMessagesIsSet(boolean value) {
    if (!value) {
      this.messages = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((FRStatusCode)value);
      }
      break;

    case STATUS_COMMENT:
      if (value == null) {
        unsetStatusComment();
      } else {
        setStatusComment((String)value);
      }
      break;

    case MESSAGES:
      if (value == null) {
        unsetMessages();
      } else {
        setMessages((List<MessageFrequency>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case STATUS:
      return getStatus();

    case STATUS_COMMENT:
      return getStatusComment();

    case MESSAGES:
      return getMessages();

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
    case STATUS_COMMENT:
      return isSetStatusComment();
    case MESSAGES:
      return isSetMessages();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof WhoIsWorkingReply)
      return this.equals((WhoIsWorkingReply)that);
    return false;
  }

  public boolean equals(WhoIsWorkingReply that) {
    if (that == null)
      return false;

    boolean this_present_status = true && this.isSetStatus();
    boolean that_present_status = true && that.isSetStatus();
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (!this.status.equals(that.status))
        return false;
    }

    boolean this_present_statusComment = true && this.isSetStatusComment();
    boolean that_present_statusComment = true && that.isSetStatusComment();
    if (this_present_statusComment || that_present_statusComment) {
      if (!(this_present_statusComment && that_present_statusComment))
        return false;
      if (!this.statusComment.equals(that.statusComment))
        return false;
    }

    boolean this_present_messages = true && this.isSetMessages();
    boolean that_present_messages = true && that.isSetMessages();
    if (this_present_messages || that_present_messages) {
      if (!(this_present_messages && that_present_messages))
        return false;
      if (!this.messages.equals(that.messages))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_status = true && (isSetStatus());
    builder.append(present_status);
    if (present_status)
      builder.append(status.getValue());

    boolean present_statusComment = true && (isSetStatusComment());
    builder.append(present_statusComment);
    if (present_statusComment)
      builder.append(statusComment);

    boolean present_messages = true && (isSetMessages());
    builder.append(present_messages);
    if (present_messages)
      builder.append(messages);

    return builder.toHashCode();
  }

  public int compareTo(WhoIsWorkingReply other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    WhoIsWorkingReply typedOther = (WhoIsWorkingReply)other;

    lastComparison = Boolean.valueOf(isSetStatus()).compareTo(typedOther.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.status, typedOther.status);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStatusComment()).compareTo(typedOther.isSetStatusComment());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatusComment()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.statusComment, typedOther.statusComment);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMessages()).compareTo(typedOther.isSetMessages());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessages()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.messages, typedOther.messages);
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
        case 1: // STATUS
          if (field.type == org.apache.thrift.protocol.TType.I32) {
            this.status = FRStatusCode.findByValue(iprot.readI32());
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // STATUS_COMMENT
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.statusComment = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // MESSAGES
          if (field.type == org.apache.thrift.protocol.TType.LIST) {
            {
              org.apache.thrift.protocol.TList _list34 = iprot.readListBegin();
              this.messages = new ArrayList<MessageFrequency>(_list34.size);
              for (int _i35 = 0; _i35 < _list34.size; ++_i35)
              {
                MessageFrequency _elem36; // required
                _elem36 = new MessageFrequency();
                _elem36.read(iprot);
                this.messages.add(_elem36);
              }
              iprot.readListEnd();
            }
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
    if (this.status != null) {
      oprot.writeFieldBegin(STATUS_FIELD_DESC);
      oprot.writeI32(this.status.getValue());
      oprot.writeFieldEnd();
    }
    if (this.statusComment != null) {
      oprot.writeFieldBegin(STATUS_COMMENT_FIELD_DESC);
      oprot.writeString(this.statusComment);
      oprot.writeFieldEnd();
    }
    if (this.messages != null) {
      if (isSetMessages()) {
        oprot.writeFieldBegin(MESSAGES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, this.messages.size()));
          for (MessageFrequency _iter37 : this.messages)
          {
            _iter37.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("WhoIsWorkingReply(");
    boolean first = true;

    sb.append("status:");
    if (this.status == null) {
      sb.append("null");
    } else {
      sb.append(this.status);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("statusComment:");
    if (this.statusComment == null) {
      sb.append("null");
    } else {
      sb.append(this.statusComment);
    }
    first = false;
    if (isSetMessages()) {
      if (!first) sb.append(", ");
      sb.append("messages:");
      if (this.messages == null) {
        sb.append("null");
      } else {
        sb.append(this.messages);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (status == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'status' was not present! Struct: " + toString());
    }
    if (statusComment == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'statusComment' was not present! Struct: " + toString());
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

