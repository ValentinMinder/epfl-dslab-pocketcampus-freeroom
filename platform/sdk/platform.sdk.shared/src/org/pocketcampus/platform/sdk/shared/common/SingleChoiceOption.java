/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.platform.sdk.shared.common;

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

public class SingleChoiceOption implements org.apache.thrift.TBase<SingleChoiceOption, SingleChoiceOption._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("SingleChoiceOption");

  private static final org.apache.thrift.protocol.TField SINGLE_CHOICE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("singleChoiceId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField CHOICES_FIELD_DESC = new org.apache.thrift.protocol.TField("choices", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField DEFAULT_CHOICE_FIELD_DESC = new org.apache.thrift.protocol.TField("defaultChoice", org.apache.thrift.protocol.TType.STRUCT, (short)4);

  public long singleChoiceId; // required
  public String name; // required
  public List<Choice> choices; // required
  public Choice defaultChoice; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SINGLE_CHOICE_ID((short)1, "singleChoiceId"),
    NAME((short)2, "name"),
    CHOICES((short)3, "choices"),
    DEFAULT_CHOICE((short)4, "defaultChoice");

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
        case 1: // SINGLE_CHOICE_ID
          return SINGLE_CHOICE_ID;
        case 2: // NAME
          return NAME;
        case 3: // CHOICES
          return CHOICES;
        case 4: // DEFAULT_CHOICE
          return DEFAULT_CHOICE;
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
  private static final int __SINGLECHOICEID_ISSET_ID = 0;
  private BitSet __isset_bit_vector = new BitSet(1);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SINGLE_CHOICE_ID, new org.apache.thrift.meta_data.FieldMetaData("singleChoiceId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Id")));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.CHOICES, new org.apache.thrift.meta_data.FieldMetaData("choices", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Choice.class))));
    tmpMap.put(_Fields.DEFAULT_CHOICE, new org.apache.thrift.meta_data.FieldMetaData("defaultChoice", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Choice.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(SingleChoiceOption.class, metaDataMap);
  }

  public SingleChoiceOption() {
  }

  public SingleChoiceOption(
    long singleChoiceId,
    String name,
    List<Choice> choices,
    Choice defaultChoice)
  {
    this();
    this.singleChoiceId = singleChoiceId;
    setSingleChoiceIdIsSet(true);
    this.name = name;
    this.choices = choices;
    this.defaultChoice = defaultChoice;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public SingleChoiceOption(SingleChoiceOption other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.singleChoiceId = other.singleChoiceId;
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetChoices()) {
      List<Choice> __this__choices = new ArrayList<Choice>();
      for (Choice other_element : other.choices) {
        __this__choices.add(new Choice(other_element));
      }
      this.choices = __this__choices;
    }
    if (other.isSetDefaultChoice()) {
      this.defaultChoice = new Choice(other.defaultChoice);
    }
  }

  public SingleChoiceOption deepCopy() {
    return new SingleChoiceOption(this);
  }

  @Override
  public void clear() {
    setSingleChoiceIdIsSet(false);
    this.singleChoiceId = 0;
    this.name = null;
    this.choices = null;
    this.defaultChoice = null;
  }

  public long getSingleChoiceId() {
    return this.singleChoiceId;
  }

  public SingleChoiceOption setSingleChoiceId(long singleChoiceId) {
    this.singleChoiceId = singleChoiceId;
    setSingleChoiceIdIsSet(true);
    return this;
  }

  public void unsetSingleChoiceId() {
    __isset_bit_vector.clear(__SINGLECHOICEID_ISSET_ID);
  }

  /** Returns true if field singleChoiceId is set (has been assigned a value) and false otherwise */
  public boolean isSetSingleChoiceId() {
    return __isset_bit_vector.get(__SINGLECHOICEID_ISSET_ID);
  }

  public void setSingleChoiceIdIsSet(boolean value) {
    __isset_bit_vector.set(__SINGLECHOICEID_ISSET_ID, value);
  }

  public String getName() {
    return this.name;
  }

  public SingleChoiceOption setName(String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public int getChoicesSize() {
    return (this.choices == null) ? 0 : this.choices.size();
  }

  public java.util.Iterator<Choice> getChoicesIterator() {
    return (this.choices == null) ? null : this.choices.iterator();
  }

  public void addToChoices(Choice elem) {
    if (this.choices == null) {
      this.choices = new ArrayList<Choice>();
    }
    this.choices.add(elem);
  }

  public List<Choice> getChoices() {
    return this.choices;
  }

  public SingleChoiceOption setChoices(List<Choice> choices) {
    this.choices = choices;
    return this;
  }

  public void unsetChoices() {
    this.choices = null;
  }

  /** Returns true if field choices is set (has been assigned a value) and false otherwise */
  public boolean isSetChoices() {
    return this.choices != null;
  }

  public void setChoicesIsSet(boolean value) {
    if (!value) {
      this.choices = null;
    }
  }

  public Choice getDefaultChoice() {
    return this.defaultChoice;
  }

  public SingleChoiceOption setDefaultChoice(Choice defaultChoice) {
    this.defaultChoice = defaultChoice;
    return this;
  }

  public void unsetDefaultChoice() {
    this.defaultChoice = null;
  }

  /** Returns true if field defaultChoice is set (has been assigned a value) and false otherwise */
  public boolean isSetDefaultChoice() {
    return this.defaultChoice != null;
  }

  public void setDefaultChoiceIsSet(boolean value) {
    if (!value) {
      this.defaultChoice = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case SINGLE_CHOICE_ID:
      if (value == null) {
        unsetSingleChoiceId();
      } else {
        setSingleChoiceId((Long)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case CHOICES:
      if (value == null) {
        unsetChoices();
      } else {
        setChoices((List<Choice>)value);
      }
      break;

    case DEFAULT_CHOICE:
      if (value == null) {
        unsetDefaultChoice();
      } else {
        setDefaultChoice((Choice)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case SINGLE_CHOICE_ID:
      return Long.valueOf(getSingleChoiceId());

    case NAME:
      return getName();

    case CHOICES:
      return getChoices();

    case DEFAULT_CHOICE:
      return getDefaultChoice();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case SINGLE_CHOICE_ID:
      return isSetSingleChoiceId();
    case NAME:
      return isSetName();
    case CHOICES:
      return isSetChoices();
    case DEFAULT_CHOICE:
      return isSetDefaultChoice();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof SingleChoiceOption)
      return this.equals((SingleChoiceOption)that);
    return false;
  }

  public boolean equals(SingleChoiceOption that) {
    if (that == null)
      return false;

    boolean this_present_singleChoiceId = true;
    boolean that_present_singleChoiceId = true;
    if (this_present_singleChoiceId || that_present_singleChoiceId) {
      if (!(this_present_singleChoiceId && that_present_singleChoiceId))
        return false;
      if (this.singleChoiceId != that.singleChoiceId)
        return false;
    }

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_choices = true && this.isSetChoices();
    boolean that_present_choices = true && that.isSetChoices();
    if (this_present_choices || that_present_choices) {
      if (!(this_present_choices && that_present_choices))
        return false;
      if (!this.choices.equals(that.choices))
        return false;
    }

    boolean this_present_defaultChoice = true && this.isSetDefaultChoice();
    boolean that_present_defaultChoice = true && that.isSetDefaultChoice();
    if (this_present_defaultChoice || that_present_defaultChoice) {
      if (!(this_present_defaultChoice && that_present_defaultChoice))
        return false;
      if (!this.defaultChoice.equals(that.defaultChoice))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_singleChoiceId = true;
    builder.append(present_singleChoiceId);
    if (present_singleChoiceId)
      builder.append(singleChoiceId);

    boolean present_name = true && (isSetName());
    builder.append(present_name);
    if (present_name)
      builder.append(name);

    boolean present_choices = true && (isSetChoices());
    builder.append(present_choices);
    if (present_choices)
      builder.append(choices);

    boolean present_defaultChoice = true && (isSetDefaultChoice());
    builder.append(present_defaultChoice);
    if (present_defaultChoice)
      builder.append(defaultChoice);

    return builder.toHashCode();
  }

  public int compareTo(SingleChoiceOption other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    SingleChoiceOption typedOther = (SingleChoiceOption)other;

    lastComparison = Boolean.valueOf(isSetSingleChoiceId()).compareTo(typedOther.isSetSingleChoiceId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSingleChoiceId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.singleChoiceId, typedOther.singleChoiceId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(typedOther.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, typedOther.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetChoices()).compareTo(typedOther.isSetChoices());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetChoices()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.choices, typedOther.choices);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDefaultChoice()).compareTo(typedOther.isSetDefaultChoice());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDefaultChoice()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.defaultChoice, typedOther.defaultChoice);
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
        case 1: // SINGLE_CHOICE_ID
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.singleChoiceId = iprot.readI64();
            setSingleChoiceIdIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // NAME
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.name = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // CHOICES
          if (field.type == org.apache.thrift.protocol.TType.LIST) {
            {
              org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
              this.choices = new ArrayList<Choice>(_list0.size);
              for (int _i1 = 0; _i1 < _list0.size; ++_i1)
              {
                Choice _elem2; // required
                _elem2 = new Choice();
                _elem2.read(iprot);
                this.choices.add(_elem2);
              }
              iprot.readListEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // DEFAULT_CHOICE
          if (field.type == org.apache.thrift.protocol.TType.STRUCT) {
            this.defaultChoice = new Choice();
            this.defaultChoice.read(iprot);
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
    if (!isSetSingleChoiceId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'singleChoiceId' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(SINGLE_CHOICE_ID_FIELD_DESC);
    oprot.writeI64(this.singleChoiceId);
    oprot.writeFieldEnd();
    if (this.name != null) {
      oprot.writeFieldBegin(NAME_FIELD_DESC);
      oprot.writeString(this.name);
      oprot.writeFieldEnd();
    }
    if (this.choices != null) {
      oprot.writeFieldBegin(CHOICES_FIELD_DESC);
      {
        oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, this.choices.size()));
        for (Choice _iter3 : this.choices)
        {
          _iter3.write(oprot);
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.defaultChoice != null) {
      oprot.writeFieldBegin(DEFAULT_CHOICE_FIELD_DESC);
      this.defaultChoice.write(oprot);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("SingleChoiceOption(");
    boolean first = true;

    sb.append("singleChoiceId:");
    sb.append(this.singleChoiceId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("choices:");
    if (this.choices == null) {
      sb.append("null");
    } else {
      sb.append(this.choices);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("defaultChoice:");
    if (this.defaultChoice == null) {
      sb.append("null");
    } else {
      sb.append(this.defaultChoice);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'singleChoiceId' because it's a primitive and you chose the non-beans generator.
    if (name == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'name' was not present! Struct: " + toString());
    }
    if (choices == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'choices' was not present! Struct: " + toString());
    }
    if (defaultChoice == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'defaultChoice' was not present! Struct: " + toString());
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
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bit_vector = new BitSet(1);
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

}

