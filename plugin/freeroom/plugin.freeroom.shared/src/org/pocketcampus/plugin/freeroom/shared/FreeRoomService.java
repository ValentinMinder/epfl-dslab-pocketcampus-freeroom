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

public class FreeRoomService {

  public interface Iface {

    public Set<FRRoom> getFreeRoomsFromTime(FRPeriodOfTime period) throws org.apache.thrift.TException;

  }

  public interface AsyncIface {

    public void getFreeRoomsFromTime(FRPeriodOfTime period, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.getFreeRoomsFromTime_call> resultHandler) throws org.apache.thrift.TException;

  }

  public static class Client extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
      public Factory() {}
      public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
        return new Client(prot);
      }
      public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        return new Client(iprot, oprot);
      }
    }

    public Client(org.apache.thrift.protocol.TProtocol prot)
    {
      super(prot, prot);
    }

    public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
      super(iprot, oprot);
    }

    public Set<FRRoom> getFreeRoomsFromTime(FRPeriodOfTime period) throws org.apache.thrift.TException
    {
      send_getFreeRoomsFromTime(period);
      return recv_getFreeRoomsFromTime();
    }

    public void send_getFreeRoomsFromTime(FRPeriodOfTime period) throws org.apache.thrift.TException
    {
      getFreeRoomsFromTime_args args = new getFreeRoomsFromTime_args();
      args.setPeriod(period);
      sendBase("getFreeRoomsFromTime", args);
    }

    public Set<FRRoom> recv_getFreeRoomsFromTime() throws org.apache.thrift.TException
    {
      getFreeRoomsFromTime_result result = new getFreeRoomsFromTime_result();
      receiveBase(result, "getFreeRoomsFromTime");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "getFreeRoomsFromTime failed: unknown result");
    }

  }
  public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
    public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;
      public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
        this.clientManager = clientManager;
        this.protocolFactory = protocolFactory;
      }
      public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
        return new AsyncClient(protocolFactory, clientManager, transport);
      }
    }

    public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
      super(protocolFactory, clientManager, transport);
    }

    public void getFreeRoomsFromTime(FRPeriodOfTime period, org.apache.thrift.async.AsyncMethodCallback<getFreeRoomsFromTime_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      getFreeRoomsFromTime_call method_call = new getFreeRoomsFromTime_call(period, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class getFreeRoomsFromTime_call extends org.apache.thrift.async.TAsyncMethodCall {
      private FRPeriodOfTime period;
      public getFreeRoomsFromTime_call(FRPeriodOfTime period, org.apache.thrift.async.AsyncMethodCallback<getFreeRoomsFromTime_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.period = period;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("getFreeRoomsFromTime", org.apache.thrift.protocol.TMessageType.CALL, 0));
        getFreeRoomsFromTime_args args = new getFreeRoomsFromTime_args();
        args.setPeriod(period);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public Set<FRRoom> getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_getFreeRoomsFromTime();
      }
    }

  }

  public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor implements org.apache.thrift.TProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());
    public Processor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
    }

    protected Processor(I iface, Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends Iface> Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> getProcessMap(Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      processMap.put("getFreeRoomsFromTime", new getFreeRoomsFromTime());
      return processMap;
    }

    private static class getFreeRoomsFromTime<I extends Iface> extends org.apache.thrift.ProcessFunction<I, getFreeRoomsFromTime_args> {
      public getFreeRoomsFromTime() {
        super("getFreeRoomsFromTime");
      }

      protected getFreeRoomsFromTime_args getEmptyArgsInstance() {
        return new getFreeRoomsFromTime_args();
      }

      protected getFreeRoomsFromTime_result getResult(I iface, getFreeRoomsFromTime_args args) throws org.apache.thrift.TException {
        getFreeRoomsFromTime_result result = new getFreeRoomsFromTime_result();
        result.success = iface.getFreeRoomsFromTime(args.period);
        return result;
      }
    }

  }

  public static class getFreeRoomsFromTime_args implements org.apache.thrift.TBase<getFreeRoomsFromTime_args, getFreeRoomsFromTime_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("getFreeRoomsFromTime_args");

    private static final org.apache.thrift.protocol.TField PERIOD_FIELD_DESC = new org.apache.thrift.protocol.TField("period", org.apache.thrift.protocol.TType.STRUCT, (short)1);

    private FRPeriodOfTime period; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      PERIOD((short)1, "period");

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
          case 1: // PERIOD
            return PERIOD;
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
      tmpMap.put(_Fields.PERIOD, new org.apache.thrift.meta_data.FieldMetaData("period", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, FRPeriodOfTime.class)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(getFreeRoomsFromTime_args.class, metaDataMap);
    }

    public getFreeRoomsFromTime_args() {
    }

    public getFreeRoomsFromTime_args(
      FRPeriodOfTime period)
    {
      this();
      this.period = period;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public getFreeRoomsFromTime_args(getFreeRoomsFromTime_args other) {
      if (other.isSetPeriod()) {
        this.period = new FRPeriodOfTime(other.period);
      }
    }

    public getFreeRoomsFromTime_args deepCopy() {
      return new getFreeRoomsFromTime_args(this);
    }

    @Override
    public void clear() {
      this.period = null;
    }

    public FRPeriodOfTime getPeriod() {
      return this.period;
    }

    public getFreeRoomsFromTime_args setPeriod(FRPeriodOfTime period) {
      this.period = period;
      return this;
    }

    public void unsetPeriod() {
      this.period = null;
    }

    /** Returns true if field period is set (has been assigned a value) and false otherwise */
    public boolean isSetPeriod() {
      return this.period != null;
    }

    public void setPeriodIsSet(boolean value) {
      if (!value) {
        this.period = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case PERIOD:
        if (value == null) {
          unsetPeriod();
        } else {
          setPeriod((FRPeriodOfTime)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case PERIOD:
        return getPeriod();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case PERIOD:
        return isSetPeriod();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof getFreeRoomsFromTime_args)
        return this.equals((getFreeRoomsFromTime_args)that);
      return false;
    }

    public boolean equals(getFreeRoomsFromTime_args that) {
      if (that == null)
        return false;

      boolean this_present_period = true && this.isSetPeriod();
      boolean that_present_period = true && that.isSetPeriod();
      if (this_present_period || that_present_period) {
        if (!(this_present_period && that_present_period))
          return false;
        if (!this.period.equals(that.period))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      HashCodeBuilder builder = new HashCodeBuilder();

      boolean present_period = true && (isSetPeriod());
      builder.append(present_period);
      if (present_period)
        builder.append(period);

      return builder.toHashCode();
    }

    public int compareTo(getFreeRoomsFromTime_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      getFreeRoomsFromTime_args typedOther = (getFreeRoomsFromTime_args)other;

      lastComparison = Boolean.valueOf(isSetPeriod()).compareTo(typedOther.isSetPeriod());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetPeriod()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.period, typedOther.period);
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
          case 1: // PERIOD
            if (field.type == org.apache.thrift.protocol.TType.STRUCT) {
              this.period = new FRPeriodOfTime();
              this.period.read(iprot);
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
      if (this.period != null) {
        oprot.writeFieldBegin(PERIOD_FIELD_DESC);
        this.period.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("getFreeRoomsFromTime_args(");
      boolean first = true;

      sb.append("period:");
      if (this.period == null) {
        sb.append("null");
      } else {
        sb.append(this.period);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
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

  public static class getFreeRoomsFromTime_result implements org.apache.thrift.TBase<getFreeRoomsFromTime_result, getFreeRoomsFromTime_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("getFreeRoomsFromTime_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.SET, (short)0);

    private Set<FRRoom> success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

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
          case 0: // SUCCESS
            return SUCCESS;
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
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET, 
              new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, FRRoom.class))));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(getFreeRoomsFromTime_result.class, metaDataMap);
    }

    public getFreeRoomsFromTime_result() {
    }

    public getFreeRoomsFromTime_result(
      Set<FRRoom> success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public getFreeRoomsFromTime_result(getFreeRoomsFromTime_result other) {
      if (other.isSetSuccess()) {
        Set<FRRoom> __this__success = new HashSet<FRRoom>();
        for (FRRoom other_element : other.success) {
          __this__success.add(new FRRoom(other_element));
        }
        this.success = __this__success;
      }
    }

    public getFreeRoomsFromTime_result deepCopy() {
      return new getFreeRoomsFromTime_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public int getSuccessSize() {
      return (this.success == null) ? 0 : this.success.size();
    }

    public java.util.Iterator<FRRoom> getSuccessIterator() {
      return (this.success == null) ? null : this.success.iterator();
    }

    public void addToSuccess(FRRoom elem) {
      if (this.success == null) {
        this.success = new HashSet<FRRoom>();
      }
      this.success.add(elem);
    }

    public Set<FRRoom> getSuccess() {
      return this.success;
    }

    public getFreeRoomsFromTime_result setSuccess(Set<FRRoom> success) {
      this.success = success;
      return this;
    }

    public void unsetSuccess() {
      this.success = null;
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return this.success != null;
    }

    public void setSuccessIsSet(boolean value) {
      if (!value) {
        this.success = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((Set<FRRoom>)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof getFreeRoomsFromTime_result)
        return this.equals((getFreeRoomsFromTime_result)that);
      return false;
    }

    public boolean equals(getFreeRoomsFromTime_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true && this.isSetSuccess();
      boolean that_present_success = true && that.isSetSuccess();
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (!this.success.equals(that.success))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      HashCodeBuilder builder = new HashCodeBuilder();

      boolean present_success = true && (isSetSuccess());
      builder.append(present_success);
      if (present_success)
        builder.append(success);

      return builder.toHashCode();
    }

    public int compareTo(getFreeRoomsFromTime_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      getFreeRoomsFromTime_result typedOther = (getFreeRoomsFromTime_result)other;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(typedOther.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, typedOther.success);
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
          case 0: // SUCCESS
            if (field.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set0 = iprot.readSetBegin();
                this.success = new HashSet<FRRoom>(2*_set0.size);
                for (int _i1 = 0; _i1 < _set0.size; ++_i1)
                {
                  FRRoom _elem2; // required
                  _elem2 = new FRRoom();
                  _elem2.read(iprot);
                  this.success.add(_elem2);
                }
                iprot.readSetEnd();
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
      oprot.writeStructBegin(STRUCT_DESC);

      if (this.isSetSuccess()) {
        oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.STRUCT, this.success.size()));
          for (FRRoom _iter3 : this.success)
          {
            _iter3.write(oprot);
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("getFreeRoomsFromTime_result(");
      boolean first = true;

      sb.append("success:");
      if (this.success == null) {
        sb.append("null");
      } else {
        sb.append(this.success);
      }
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
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

}
