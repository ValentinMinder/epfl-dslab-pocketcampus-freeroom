/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.pocketcampus.plugin.map.shared;

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
public class MapLayersResponse implements org.apache.thrift.TBase<MapLayersResponse, MapLayersResponse._Fields>, java.io.Serializable, Cloneable, Comparable<MapLayersResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MapLayersResponse");

  private static final org.apache.thrift.protocol.TField STATUS_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("statusCode", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField LAYERS_FIELD_DESC = new org.apache.thrift.protocol.TField("layers", org.apache.thrift.protocol.TType.MAP, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new MapLayersResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new MapLayersResponseTupleSchemeFactory());
  }

  private MapStatusCode statusCode; // required
  private Map<Long,MapLayer> layers; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see MapStatusCode
     */
    STATUS_CODE((short)1, "statusCode"),
    LAYERS((short)2, "layers");

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
        case 1: // STATUS_CODE
          return STATUS_CODE;
        case 2: // LAYERS
          return LAYERS;
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
    tmpMap.put(_Fields.STATUS_CODE, new org.apache.thrift.meta_data.FieldMetaData("statusCode", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, MapStatusCode.class)));
    tmpMap.put(_Fields.LAYERS, new org.apache.thrift.meta_data.FieldMetaData("layers", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, MapLayer.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MapLayersResponse.class, metaDataMap);
  }

  public MapLayersResponse() {
  }

  public MapLayersResponse(
    MapStatusCode statusCode,
    Map<Long,MapLayer> layers)
  {
    this();
    this.statusCode = statusCode;
    this.layers = layers;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MapLayersResponse(MapLayersResponse other) {
    if (other.isSetStatusCode()) {
      this.statusCode = other.statusCode;
    }
    if (other.isSetLayers()) {
      Map<Long,MapLayer> __this__layers = new HashMap<Long,MapLayer>(other.layers.size());
      for (Map.Entry<Long, MapLayer> other_element : other.layers.entrySet()) {

        Long other_element_key = other_element.getKey();
        MapLayer other_element_value = other_element.getValue();

        Long __this__layers_copy_key = other_element_key;

        MapLayer __this__layers_copy_value = new MapLayer(other_element_value);

        __this__layers.put(__this__layers_copy_key, __this__layers_copy_value);
      }
      this.layers = __this__layers;
    }
  }

  public MapLayersResponse deepCopy() {
    return new MapLayersResponse(this);
  }

  @Override
  public void clear() {
    this.statusCode = null;
    this.layers = null;
  }

  /**
   * 
   * @see MapStatusCode
   */
  public MapStatusCode getStatusCode() {
    return this.statusCode;
  }

  /**
   * 
   * @see MapStatusCode
   */
  public MapLayersResponse setStatusCode(MapStatusCode statusCode) {
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

  public int getLayersSize() {
    return (this.layers == null) ? 0 : this.layers.size();
  }

  public void putToLayers(long key, MapLayer val) {
    if (this.layers == null) {
      this.layers = new HashMap<Long,MapLayer>();
    }
    this.layers.put(key, val);
  }

  public Map<Long,MapLayer> getLayers() {
    return this.layers;
  }

  public MapLayersResponse setLayers(Map<Long,MapLayer> layers) {
    this.layers = layers;
    return this;
  }

  public void unsetLayers() {
    this.layers = null;
  }

  /** Returns true if field layers is set (has been assigned a value) and false otherwise */
  public boolean isSetLayers() {
    return this.layers != null;
  }

  public void setLayersIsSet(boolean value) {
    if (!value) {
      this.layers = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case STATUS_CODE:
      if (value == null) {
        unsetStatusCode();
      } else {
        setStatusCode((MapStatusCode)value);
      }
      break;

    case LAYERS:
      if (value == null) {
        unsetLayers();
      } else {
        setLayers((Map<Long,MapLayer>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case STATUS_CODE:
      return getStatusCode();

    case LAYERS:
      return getLayers();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case STATUS_CODE:
      return isSetStatusCode();
    case LAYERS:
      return isSetLayers();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MapLayersResponse)
      return this.equals((MapLayersResponse)that);
    return false;
  }

  public boolean equals(MapLayersResponse that) {
    if (that == null)
      return false;

    boolean this_present_statusCode = true && this.isSetStatusCode();
    boolean that_present_statusCode = true && that.isSetStatusCode();
    if (this_present_statusCode || that_present_statusCode) {
      if (!(this_present_statusCode && that_present_statusCode))
        return false;
      if (!this.statusCode.equals(that.statusCode))
        return false;
    }

    boolean this_present_layers = true && this.isSetLayers();
    boolean that_present_layers = true && that.isSetLayers();
    if (this_present_layers || that_present_layers) {
      if (!(this_present_layers && that_present_layers))
        return false;
      if (!this.layers.equals(that.layers))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_statusCode = true && (isSetStatusCode());
    list.add(present_statusCode);
    if (present_statusCode)
      list.add(statusCode.getValue());

    boolean present_layers = true && (isSetLayers());
    list.add(present_layers);
    if (present_layers)
      list.add(layers);

    return list.hashCode();
  }

  @Override
  public int compareTo(MapLayersResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetStatusCode()).compareTo(other.isSetStatusCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatusCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.statusCode, other.statusCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLayers()).compareTo(other.isSetLayers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLayers()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.layers, other.layers);
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
    StringBuilder sb = new StringBuilder("MapLayersResponse(");
    boolean first = true;

    sb.append("statusCode:");
    if (this.statusCode == null) {
      sb.append("null");
    } else {
      sb.append(this.statusCode);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("layers:");
    if (this.layers == null) {
      sb.append("null");
    } else {
      sb.append(this.layers);
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
    if (layers == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'layers' was not present! Struct: " + toString());
    }
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class MapLayersResponseStandardSchemeFactory implements SchemeFactory {
    public MapLayersResponseStandardScheme getScheme() {
      return new MapLayersResponseStandardScheme();
    }
  }

  private static class MapLayersResponseStandardScheme extends StandardScheme<MapLayersResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MapLayersResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STATUS_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.statusCode = org.pocketcampus.plugin.map.shared.MapStatusCode.findByValue(iprot.readI32());
              struct.setStatusCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // LAYERS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
                struct.layers = new HashMap<Long,MapLayer>(2*_map0.size);
                long _key1;
                MapLayer _val2;
                for (int _i3 = 0; _i3 < _map0.size; ++_i3)
                {
                  _key1 = iprot.readI64();
                  _val2 = new MapLayer();
                  _val2.read(iprot);
                  struct.layers.put(_key1, _val2);
                }
                iprot.readMapEnd();
              }
              struct.setLayersIsSet(true);
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
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, MapLayersResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.statusCode != null) {
        oprot.writeFieldBegin(STATUS_CODE_FIELD_DESC);
        oprot.writeI32(struct.statusCode.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.layers != null) {
        oprot.writeFieldBegin(LAYERS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.STRUCT, struct.layers.size()));
          for (Map.Entry<Long, MapLayer> _iter4 : struct.layers.entrySet())
          {
            oprot.writeI64(_iter4.getKey());
            _iter4.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MapLayersResponseTupleSchemeFactory implements SchemeFactory {
    public MapLayersResponseTupleScheme getScheme() {
      return new MapLayersResponseTupleScheme();
    }
  }

  private static class MapLayersResponseTupleScheme extends TupleScheme<MapLayersResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MapLayersResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.statusCode.getValue());
      {
        oprot.writeI32(struct.layers.size());
        for (Map.Entry<Long, MapLayer> _iter5 : struct.layers.entrySet())
        {
          oprot.writeI64(_iter5.getKey());
          _iter5.getValue().write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MapLayersResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.statusCode = org.pocketcampus.plugin.map.shared.MapStatusCode.findByValue(iprot.readI32());
      struct.setStatusCodeIsSet(true);
      {
        org.apache.thrift.protocol.TMap _map6 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I64, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.layers = new HashMap<Long,MapLayer>(2*_map6.size);
        long _key7;
        MapLayer _val8;
        for (int _i9 = 0; _i9 < _map6.size; ++_i9)
        {
          _key7 = iprot.readI64();
          _val8 = new MapLayer();
          _val8.read(iprot);
          struct.layers.put(_key7, _val8);
        }
      }
      struct.setLayersIsSet(true);
    }
  }

}

