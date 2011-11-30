/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.transport.shared;

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

public class Railway implements org.apache.thrift.TBase<Railway, Railway._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Railway");

  private static final org.apache.thrift.protocol.TField NODES__FIELD_DESC = new org.apache.thrift.protocol.TField("nodes_", org.apache.thrift.protocol.TType.MAP, (short)1);
  private static final org.apache.thrift.protocol.TField WAYS__FIELD_DESC = new org.apache.thrift.protocol.TField("ways_", org.apache.thrift.protocol.TType.MAP, (short)2);
  private static final org.apache.thrift.protocol.TField MEMBERS__FIELD_DESC = new org.apache.thrift.protocol.TField("members_", org.apache.thrift.protocol.TType.SET, (short)3);
  private static final org.apache.thrift.protocol.TField RAILWAY__FIELD_DESC = new org.apache.thrift.protocol.TField("railway_", org.apache.thrift.protocol.TType.SET, (short)4);
  private static final org.apache.thrift.protocol.TField STOP_NODES__FIELD_DESC = new org.apache.thrift.protocol.TField("stopNodes_", org.apache.thrift.protocol.TType.MAP, (short)5);

  public Map<Integer,RailwayNode> nodes_; // required
  public Map<Integer,RailwayWay> ways_; // required
  public Set<RailwayMember> members_; // required
  public Set<RailwayNode> railway_; // required
  public Map<Integer,RailwayNode> stopNodes_; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NODES_((short)1, "nodes_"),
    WAYS_((short)2, "ways_"),
    MEMBERS_((short)3, "members_"),
    RAILWAY_((short)4, "railway_"),
    STOP_NODES_((short)5, "stopNodes_");

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
        case 1: // NODES_
          return NODES_;
        case 2: // WAYS_
          return WAYS_;
        case 3: // MEMBERS_
          return MEMBERS_;
        case 4: // RAILWAY_
          return RAILWAY_;
        case 5: // STOP_NODES_
          return STOP_NODES_;
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
    tmpMap.put(_Fields.NODES_, new org.apache.thrift.meta_data.FieldMetaData("nodes_", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RailwayNode.class))));
    tmpMap.put(_Fields.WAYS_, new org.apache.thrift.meta_data.FieldMetaData("ways_", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RailwayWay.class))));
    tmpMap.put(_Fields.MEMBERS_, new org.apache.thrift.meta_data.FieldMetaData("members_", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RailwayMember.class))));
    tmpMap.put(_Fields.RAILWAY_, new org.apache.thrift.meta_data.FieldMetaData("railway_", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RailwayNode.class))));
    tmpMap.put(_Fields.STOP_NODES_, new org.apache.thrift.meta_data.FieldMetaData("stopNodes_", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, RailwayNode.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Railway.class, metaDataMap);
  }

  public Railway() {
  }

  public Railway(
    Map<Integer,RailwayNode> nodes_,
    Map<Integer,RailwayWay> ways_,
    Set<RailwayMember> members_,
    Set<RailwayNode> railway_,
    Map<Integer,RailwayNode> stopNodes_)
  {
    this();
    this.nodes_ = nodes_;
    this.ways_ = ways_;
    this.members_ = members_;
    this.railway_ = railway_;
    this.stopNodes_ = stopNodes_;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Railway(Railway other) {
    if (other.isSetNodes_()) {
      Map<Integer,RailwayNode> __this__nodes_ = new HashMap<Integer,RailwayNode>();
      for (Map.Entry<Integer, RailwayNode> other_element : other.nodes_.entrySet()) {

        Integer other_element_key = other_element.getKey();
        RailwayNode other_element_value = other_element.getValue();

        Integer __this__nodes__copy_key = other_element_key;

        RailwayNode __this__nodes__copy_value = new RailwayNode(other_element_value);

        __this__nodes_.put(__this__nodes__copy_key, __this__nodes__copy_value);
      }
      this.nodes_ = __this__nodes_;
    }
    if (other.isSetWays_()) {
      Map<Integer,RailwayWay> __this__ways_ = new HashMap<Integer,RailwayWay>();
      for (Map.Entry<Integer, RailwayWay> other_element : other.ways_.entrySet()) {

        Integer other_element_key = other_element.getKey();
        RailwayWay other_element_value = other_element.getValue();

        Integer __this__ways__copy_key = other_element_key;

        RailwayWay __this__ways__copy_value = new RailwayWay(other_element_value);

        __this__ways_.put(__this__ways__copy_key, __this__ways__copy_value);
      }
      this.ways_ = __this__ways_;
    }
    if (other.isSetMembers_()) {
      Set<RailwayMember> __this__members_ = new HashSet<RailwayMember>();
      for (RailwayMember other_element : other.members_) {
        __this__members_.add(new RailwayMember(other_element));
      }
      this.members_ = __this__members_;
    }
    if (other.isSetRailway_()) {
      Set<RailwayNode> __this__railway_ = new HashSet<RailwayNode>();
      for (RailwayNode other_element : other.railway_) {
        __this__railway_.add(new RailwayNode(other_element));
      }
      this.railway_ = __this__railway_;
    }
    if (other.isSetStopNodes_()) {
      Map<Integer,RailwayNode> __this__stopNodes_ = new HashMap<Integer,RailwayNode>();
      for (Map.Entry<Integer, RailwayNode> other_element : other.stopNodes_.entrySet()) {

        Integer other_element_key = other_element.getKey();
        RailwayNode other_element_value = other_element.getValue();

        Integer __this__stopNodes__copy_key = other_element_key;

        RailwayNode __this__stopNodes__copy_value = new RailwayNode(other_element_value);

        __this__stopNodes_.put(__this__stopNodes__copy_key, __this__stopNodes__copy_value);
      }
      this.stopNodes_ = __this__stopNodes_;
    }
  }

  public Railway deepCopy() {
    return new Railway(this);
  }

  @Override
  public void clear() {
    this.nodes_ = null;
    this.ways_ = null;
    this.members_ = null;
    this.railway_ = null;
    this.stopNodes_ = null;
  }

  public int getNodes_Size() {
    return (this.nodes_ == null) ? 0 : this.nodes_.size();
  }

  public void putToNodes_(int key, RailwayNode val) {
    if (this.nodes_ == null) {
      this.nodes_ = new HashMap<Integer,RailwayNode>();
    }
    this.nodes_.put(key, val);
  }

  public Map<Integer,RailwayNode> getNodes_() {
    return this.nodes_;
  }

  public Railway setNodes_(Map<Integer,RailwayNode> nodes_) {
    this.nodes_ = nodes_;
    return this;
  }

  public void unsetNodes_() {
    this.nodes_ = null;
  }

  /** Returns true if field nodes_ is set (has been assigned a value) and false otherwise */
  public boolean isSetNodes_() {
    return this.nodes_ != null;
  }

  public void setNodes_IsSet(boolean value) {
    if (!value) {
      this.nodes_ = null;
    }
  }

  public int getWays_Size() {
    return (this.ways_ == null) ? 0 : this.ways_.size();
  }

  public void putToWays_(int key, RailwayWay val) {
    if (this.ways_ == null) {
      this.ways_ = new HashMap<Integer,RailwayWay>();
    }
    this.ways_.put(key, val);
  }

  public Map<Integer,RailwayWay> getWays_() {
    return this.ways_;
  }

  public Railway setWays_(Map<Integer,RailwayWay> ways_) {
    this.ways_ = ways_;
    return this;
  }

  public void unsetWays_() {
    this.ways_ = null;
  }

  /** Returns true if field ways_ is set (has been assigned a value) and false otherwise */
  public boolean isSetWays_() {
    return this.ways_ != null;
  }

  public void setWays_IsSet(boolean value) {
    if (!value) {
      this.ways_ = null;
    }
  }

  public int getMembers_Size() {
    return (this.members_ == null) ? 0 : this.members_.size();
  }

  public java.util.Iterator<RailwayMember> getMembers_Iterator() {
    return (this.members_ == null) ? null : this.members_.iterator();
  }

  public void addToMembers_(RailwayMember elem) {
    if (this.members_ == null) {
      this.members_ = new HashSet<RailwayMember>();
    }
    this.members_.add(elem);
  }

  public Set<RailwayMember> getMembers_() {
    return this.members_;
  }

  public Railway setMembers_(Set<RailwayMember> members_) {
    this.members_ = members_;
    return this;
  }

  public void unsetMembers_() {
    this.members_ = null;
  }

  /** Returns true if field members_ is set (has been assigned a value) and false otherwise */
  public boolean isSetMembers_() {
    return this.members_ != null;
  }

  public void setMembers_IsSet(boolean value) {
    if (!value) {
      this.members_ = null;
    }
  }

  public int getRailway_Size() {
    return (this.railway_ == null) ? 0 : this.railway_.size();
  }

  public java.util.Iterator<RailwayNode> getRailway_Iterator() {
    return (this.railway_ == null) ? null : this.railway_.iterator();
  }

  public void addToRailway_(RailwayNode elem) {
    if (this.railway_ == null) {
      this.railway_ = new HashSet<RailwayNode>();
    }
    this.railway_.add(elem);
  }

  public Set<RailwayNode> getRailway_() {
    return this.railway_;
  }

  public Railway setRailway_(Set<RailwayNode> railway_) {
    this.railway_ = railway_;
    return this;
  }

  public void unsetRailway_() {
    this.railway_ = null;
  }

  /** Returns true if field railway_ is set (has been assigned a value) and false otherwise */
  public boolean isSetRailway_() {
    return this.railway_ != null;
  }

  public void setRailway_IsSet(boolean value) {
    if (!value) {
      this.railway_ = null;
    }
  }

  public int getStopNodes_Size() {
    return (this.stopNodes_ == null) ? 0 : this.stopNodes_.size();
  }

  public void putToStopNodes_(int key, RailwayNode val) {
    if (this.stopNodes_ == null) {
      this.stopNodes_ = new HashMap<Integer,RailwayNode>();
    }
    this.stopNodes_.put(key, val);
  }

  public Map<Integer,RailwayNode> getStopNodes_() {
    return this.stopNodes_;
  }

  public Railway setStopNodes_(Map<Integer,RailwayNode> stopNodes_) {
    this.stopNodes_ = stopNodes_;
    return this;
  }

  public void unsetStopNodes_() {
    this.stopNodes_ = null;
  }

  /** Returns true if field stopNodes_ is set (has been assigned a value) and false otherwise */
  public boolean isSetStopNodes_() {
    return this.stopNodes_ != null;
  }

  public void setStopNodes_IsSet(boolean value) {
    if (!value) {
      this.stopNodes_ = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NODES_:
      if (value == null) {
        unsetNodes_();
      } else {
        setNodes_((Map<Integer,RailwayNode>)value);
      }
      break;

    case WAYS_:
      if (value == null) {
        unsetWays_();
      } else {
        setWays_((Map<Integer,RailwayWay>)value);
      }
      break;

    case MEMBERS_:
      if (value == null) {
        unsetMembers_();
      } else {
        setMembers_((Set<RailwayMember>)value);
      }
      break;

    case RAILWAY_:
      if (value == null) {
        unsetRailway_();
      } else {
        setRailway_((Set<RailwayNode>)value);
      }
      break;

    case STOP_NODES_:
      if (value == null) {
        unsetStopNodes_();
      } else {
        setStopNodes_((Map<Integer,RailwayNode>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NODES_:
      return getNodes_();

    case WAYS_:
      return getWays_();

    case MEMBERS_:
      return getMembers_();

    case RAILWAY_:
      return getRailway_();

    case STOP_NODES_:
      return getStopNodes_();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NODES_:
      return isSetNodes_();
    case WAYS_:
      return isSetWays_();
    case MEMBERS_:
      return isSetMembers_();
    case RAILWAY_:
      return isSetRailway_();
    case STOP_NODES_:
      return isSetStopNodes_();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Railway)
      return this.equals((Railway)that);
    return false;
  }

  public boolean equals(Railway that) {
    if (that == null)
      return false;

    boolean this_present_nodes_ = true && this.isSetNodes_();
    boolean that_present_nodes_ = true && that.isSetNodes_();
    if (this_present_nodes_ || that_present_nodes_) {
      if (!(this_present_nodes_ && that_present_nodes_))
        return false;
      if (!this.nodes_.equals(that.nodes_))
        return false;
    }

    boolean this_present_ways_ = true && this.isSetWays_();
    boolean that_present_ways_ = true && that.isSetWays_();
    if (this_present_ways_ || that_present_ways_) {
      if (!(this_present_ways_ && that_present_ways_))
        return false;
      if (!this.ways_.equals(that.ways_))
        return false;
    }

    boolean this_present_members_ = true && this.isSetMembers_();
    boolean that_present_members_ = true && that.isSetMembers_();
    if (this_present_members_ || that_present_members_) {
      if (!(this_present_members_ && that_present_members_))
        return false;
      if (!this.members_.equals(that.members_))
        return false;
    }

    boolean this_present_railway_ = true && this.isSetRailway_();
    boolean that_present_railway_ = true && that.isSetRailway_();
    if (this_present_railway_ || that_present_railway_) {
      if (!(this_present_railway_ && that_present_railway_))
        return false;
      if (!this.railway_.equals(that.railway_))
        return false;
    }

    boolean this_present_stopNodes_ = true && this.isSetStopNodes_();
    boolean that_present_stopNodes_ = true && that.isSetStopNodes_();
    if (this_present_stopNodes_ || that_present_stopNodes_) {
      if (!(this_present_stopNodes_ && that_present_stopNodes_))
        return false;
      if (!this.stopNodes_.equals(that.stopNodes_))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_nodes_ = true && (isSetNodes_());
    builder.append(present_nodes_);
    if (present_nodes_)
      builder.append(nodes_);

    boolean present_ways_ = true && (isSetWays_());
    builder.append(present_ways_);
    if (present_ways_)
      builder.append(ways_);

    boolean present_members_ = true && (isSetMembers_());
    builder.append(present_members_);
    if (present_members_)
      builder.append(members_);

    boolean present_railway_ = true && (isSetRailway_());
    builder.append(present_railway_);
    if (present_railway_)
      builder.append(railway_);

    boolean present_stopNodes_ = true && (isSetStopNodes_());
    builder.append(present_stopNodes_);
    if (present_stopNodes_)
      builder.append(stopNodes_);

    return builder.toHashCode();
  }

  public int compareTo(Railway other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Railway typedOther = (Railway)other;

    lastComparison = Boolean.valueOf(isSetNodes_()).compareTo(typedOther.isSetNodes_());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNodes_()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.nodes_, typedOther.nodes_);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetWays_()).compareTo(typedOther.isSetWays_());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetWays_()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ways_, typedOther.ways_);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMembers_()).compareTo(typedOther.isSetMembers_());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMembers_()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.members_, typedOther.members_);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRailway_()).compareTo(typedOther.isSetRailway_());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRailway_()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.railway_, typedOther.railway_);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStopNodes_()).compareTo(typedOther.isSetStopNodes_());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStopNodes_()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.stopNodes_, typedOther.stopNodes_);
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
        case 1: // NODES_
          if (field.type == org.apache.thrift.protocol.TType.MAP) {
            {
              org.apache.thrift.protocol.TMap _map69 = iprot.readMapBegin();
              this.nodes_ = new HashMap<Integer,RailwayNode>(2*_map69.size);
              for (int _i70 = 0; _i70 < _map69.size; ++_i70)
              {
                int _key71; // required
                RailwayNode _val72; // required
                _key71 = iprot.readI32();
                _val72 = new RailwayNode();
                _val72.read(iprot);
                this.nodes_.put(_key71, _val72);
              }
              iprot.readMapEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // WAYS_
          if (field.type == org.apache.thrift.protocol.TType.MAP) {
            {
              org.apache.thrift.protocol.TMap _map73 = iprot.readMapBegin();
              this.ways_ = new HashMap<Integer,RailwayWay>(2*_map73.size);
              for (int _i74 = 0; _i74 < _map73.size; ++_i74)
              {
                int _key75; // required
                RailwayWay _val76; // required
                _key75 = iprot.readI32();
                _val76 = new RailwayWay();
                _val76.read(iprot);
                this.ways_.put(_key75, _val76);
              }
              iprot.readMapEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // MEMBERS_
          if (field.type == org.apache.thrift.protocol.TType.SET) {
            {
              org.apache.thrift.protocol.TSet _set77 = iprot.readSetBegin();
              this.members_ = new HashSet<RailwayMember>(2*_set77.size);
              for (int _i78 = 0; _i78 < _set77.size; ++_i78)
              {
                RailwayMember _elem79; // required
                _elem79 = new RailwayMember();
                _elem79.read(iprot);
                this.members_.add(_elem79);
              }
              iprot.readSetEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // RAILWAY_
          if (field.type == org.apache.thrift.protocol.TType.SET) {
            {
              org.apache.thrift.protocol.TSet _set80 = iprot.readSetBegin();
              this.railway_ = new HashSet<RailwayNode>(2*_set80.size);
              for (int _i81 = 0; _i81 < _set80.size; ++_i81)
              {
                RailwayNode _elem82; // required
                _elem82 = new RailwayNode();
                _elem82.read(iprot);
                this.railway_.add(_elem82);
              }
              iprot.readSetEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // STOP_NODES_
          if (field.type == org.apache.thrift.protocol.TType.MAP) {
            {
              org.apache.thrift.protocol.TMap _map83 = iprot.readMapBegin();
              this.stopNodes_ = new HashMap<Integer,RailwayNode>(2*_map83.size);
              for (int _i84 = 0; _i84 < _map83.size; ++_i84)
              {
                int _key85; // required
                RailwayNode _val86; // required
                _key85 = iprot.readI32();
                _val86 = new RailwayNode();
                _val86.read(iprot);
                this.stopNodes_.put(_key85, _val86);
              }
              iprot.readMapEnd();
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
    if (this.nodes_ != null) {
      oprot.writeFieldBegin(NODES__FIELD_DESC);
      {
        oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.STRUCT, this.nodes_.size()));
        for (Map.Entry<Integer, RailwayNode> _iter87 : this.nodes_.entrySet())
        {
          oprot.writeI32(_iter87.getKey());
          _iter87.getValue().write(oprot);
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.ways_ != null) {
      oprot.writeFieldBegin(WAYS__FIELD_DESC);
      {
        oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.STRUCT, this.ways_.size()));
        for (Map.Entry<Integer, RailwayWay> _iter88 : this.ways_.entrySet())
        {
          oprot.writeI32(_iter88.getKey());
          _iter88.getValue().write(oprot);
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.members_ != null) {
      oprot.writeFieldBegin(MEMBERS__FIELD_DESC);
      {
        oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRUCT, this.members_.size()));
        for (RailwayMember _iter89 : this.members_)
        {
          _iter89.write(oprot);
        }
        oprot.writeSetEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.railway_ != null) {
      oprot.writeFieldBegin(RAILWAY__FIELD_DESC);
      {
        oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRUCT, this.railway_.size()));
        for (RailwayNode _iter90 : this.railway_)
        {
          _iter90.write(oprot);
        }
        oprot.writeSetEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.stopNodes_ != null) {
      oprot.writeFieldBegin(STOP_NODES__FIELD_DESC);
      {
        oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.STRUCT, this.stopNodes_.size()));
        for (Map.Entry<Integer, RailwayNode> _iter91 : this.stopNodes_.entrySet())
        {
          oprot.writeI32(_iter91.getKey());
          _iter91.getValue().write(oprot);
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Railway(");
    boolean first = true;

    sb.append("nodes_:");
    if (this.nodes_ == null) {
      sb.append("null");
    } else {
      sb.append(this.nodes_);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("ways_:");
    if (this.ways_ == null) {
      sb.append("null");
    } else {
      sb.append(this.ways_);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("members_:");
    if (this.members_ == null) {
      sb.append("null");
    } else {
      sb.append(this.members_);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("railway_:");
    if (this.railway_ == null) {
      sb.append("null");
    } else {
      sb.append(this.railway_);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("stopNodes_:");
    if (this.stopNodes_ == null) {
      sb.append("null");
    } else {
      sb.append(this.stopNodes_);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (nodes_ == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'nodes_' was not present! Struct: " + toString());
    }
    if (ways_ == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'ways_' was not present! Struct: " + toString());
    }
    if (members_ == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'members_' was not present! Struct: " + toString());
    }
    if (railway_ == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'railway_' was not present! Struct: " + toString());
    }
    if (stopNodes_ == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'stopNodes_' was not present! Struct: " + toString());
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

