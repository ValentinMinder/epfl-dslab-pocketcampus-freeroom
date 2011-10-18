/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.news.shared;

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

public class NewsService {

  public interface Iface {

    public List<NewsItem> getNewsItems(List<String> feedUrls) throws org.apache.thrift.TException;

  }

  public interface AsyncIface {

    public void getNewsItems(List<String> feedUrls, org.apache.thrift.async.AsyncMethodCallback<AsyncClient.getNewsItems_call> resultHandler) throws org.apache.thrift.TException;

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

    public List<NewsItem> getNewsItems(List<String> feedUrls) throws org.apache.thrift.TException
    {
      send_getNewsItems(feedUrls);
      return recv_getNewsItems();
    }

    public void send_getNewsItems(List<String> feedUrls) throws org.apache.thrift.TException
    {
      getNewsItems_args args = new getNewsItems_args();
      args.setFeedUrls(feedUrls);
      sendBase("getNewsItems", args);
    }

    public List<NewsItem> recv_getNewsItems() throws org.apache.thrift.TException
    {
      getNewsItems_result result = new getNewsItems_result();
      receiveBase(result, "getNewsItems");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "getNewsItems failed: unknown result");
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

    public void getNewsItems(List<String> feedUrls, org.apache.thrift.async.AsyncMethodCallback<getNewsItems_call> resultHandler) throws org.apache.thrift.TException {
      checkReady();
      getNewsItems_call method_call = new getNewsItems_call(feedUrls, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class getNewsItems_call extends org.apache.thrift.async.TAsyncMethodCall {
      private List<String> feedUrls;
      public getNewsItems_call(List<String> feedUrls, org.apache.thrift.async.AsyncMethodCallback<getNewsItems_call> resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.feedUrls = feedUrls;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("getNewsItems", org.apache.thrift.protocol.TMessageType.CALL, 0));
        getNewsItems_args args = new getNewsItems_args();
        args.setFeedUrls(feedUrls);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public List<NewsItem> getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_getNewsItems();
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
      processMap.put("getNewsItems", new getNewsItems());
      return processMap;
    }

    private static class getNewsItems<I extends Iface> extends org.apache.thrift.ProcessFunction<I, getNewsItems_args> {
      public getNewsItems() {
        super("getNewsItems");
      }

      protected getNewsItems_args getEmptyArgsInstance() {
        return new getNewsItems_args();
      }

      protected getNewsItems_result getResult(I iface, getNewsItems_args args) throws org.apache.thrift.TException {
        getNewsItems_result result = new getNewsItems_result();
        result.success = iface.getNewsItems(args.feedUrls);
        return result;
      }
    }

  }

  public static class getNewsItems_args implements org.apache.thrift.TBase<getNewsItems_args, getNewsItems_args._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("getNewsItems_args");

    private static final org.apache.thrift.protocol.TField FEED_URLS_FIELD_DESC = new org.apache.thrift.protocol.TField("feedUrls", org.apache.thrift.protocol.TType.LIST, (short)1);

    public List<String> feedUrls; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      FEED_URLS((short)1, "feedUrls");

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
          case 1: // FEED_URLS
            return FEED_URLS;
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
      tmpMap.put(_Fields.FEED_URLS, new org.apache.thrift.meta_data.FieldMetaData("feedUrls", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
              new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(getNewsItems_args.class, metaDataMap);
    }

    public getNewsItems_args() {
    }

    public getNewsItems_args(
      List<String> feedUrls)
    {
      this();
      this.feedUrls = feedUrls;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public getNewsItems_args(getNewsItems_args other) {
      if (other.isSetFeedUrls()) {
        List<String> __this__feedUrls = new ArrayList<String>();
        for (String other_element : other.feedUrls) {
          __this__feedUrls.add(other_element);
        }
        this.feedUrls = __this__feedUrls;
      }
    }

    public getNewsItems_args deepCopy() {
      return new getNewsItems_args(this);
    }

    @Override
    public void clear() {
      this.feedUrls = null;
    }

    public int getFeedUrlsSize() {
      return (this.feedUrls == null) ? 0 : this.feedUrls.size();
    }

    public java.util.Iterator<String> getFeedUrlsIterator() {
      return (this.feedUrls == null) ? null : this.feedUrls.iterator();
    }

    public void addToFeedUrls(String elem) {
      if (this.feedUrls == null) {
        this.feedUrls = new ArrayList<String>();
      }
      this.feedUrls.add(elem);
    }

    public List<String> getFeedUrls() {
      return this.feedUrls;
    }

    public getNewsItems_args setFeedUrls(List<String> feedUrls) {
      this.feedUrls = feedUrls;
      return this;
    }

    public void unsetFeedUrls() {
      this.feedUrls = null;
    }

    /** Returns true if field feedUrls is set (has been assigned a value) and false otherwise */
    public boolean isSetFeedUrls() {
      return this.feedUrls != null;
    }

    public void setFeedUrlsIsSet(boolean value) {
      if (!value) {
        this.feedUrls = null;
      }
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case FEED_URLS:
        if (value == null) {
          unsetFeedUrls();
        } else {
          setFeedUrls((List<String>)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case FEED_URLS:
        return getFeedUrls();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case FEED_URLS:
        return isSetFeedUrls();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof getNewsItems_args)
        return this.equals((getNewsItems_args)that);
      return false;
    }

    public boolean equals(getNewsItems_args that) {
      if (that == null)
        return false;

      boolean this_present_feedUrls = true && this.isSetFeedUrls();
      boolean that_present_feedUrls = true && that.isSetFeedUrls();
      if (this_present_feedUrls || that_present_feedUrls) {
        if (!(this_present_feedUrls && that_present_feedUrls))
          return false;
        if (!this.feedUrls.equals(that.feedUrls))
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    public int compareTo(getNewsItems_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      getNewsItems_args typedOther = (getNewsItems_args)other;

      lastComparison = Boolean.valueOf(isSetFeedUrls()).compareTo(typedOther.isSetFeedUrls());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetFeedUrls()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.feedUrls, typedOther.feedUrls);
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
          case 1: // FEED_URLS
            if (field.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list4 = iprot.readListBegin();
                this.feedUrls = new ArrayList<String>(_list4.size);
                for (int _i5 = 0; _i5 < _list4.size; ++_i5)
                {
                  String _elem6; // required
                  _elem6 = iprot.readString();
                  this.feedUrls.add(_elem6);
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
      if (this.feedUrls != null) {
        oprot.writeFieldBegin(FEED_URLS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, this.feedUrls.size()));
          for (String _iter7 : this.feedUrls)
          {
            oprot.writeString(_iter7);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("getNewsItems_args(");
      boolean first = true;

      sb.append("feedUrls:");
      if (this.feedUrls == null) {
        sb.append("null");
      } else {
        sb.append(this.feedUrls);
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

  public static class getNewsItems_result implements org.apache.thrift.TBase<getNewsItems_result, getNewsItems_result._Fields>, java.io.Serializable, Cloneable   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("getNewsItems_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.LIST, (short)0);

    public List<NewsItem> success; // required

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
          new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
              new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, NewsItem.class))));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(getNewsItems_result.class, metaDataMap);
    }

    public getNewsItems_result() {
    }

    public getNewsItems_result(
      List<NewsItem> success)
    {
      this();
      this.success = success;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public getNewsItems_result(getNewsItems_result other) {
      if (other.isSetSuccess()) {
        List<NewsItem> __this__success = new ArrayList<NewsItem>();
        for (NewsItem other_element : other.success) {
          __this__success.add(new NewsItem(other_element));
        }
        this.success = __this__success;
      }
    }

    public getNewsItems_result deepCopy() {
      return new getNewsItems_result(this);
    }

    @Override
    public void clear() {
      this.success = null;
    }

    public int getSuccessSize() {
      return (this.success == null) ? 0 : this.success.size();
    }

    public java.util.Iterator<NewsItem> getSuccessIterator() {
      return (this.success == null) ? null : this.success.iterator();
    }

    public void addToSuccess(NewsItem elem) {
      if (this.success == null) {
        this.success = new ArrayList<NewsItem>();
      }
      this.success.add(elem);
    }

    public List<NewsItem> getSuccess() {
      return this.success;
    }

    public getNewsItems_result setSuccess(List<NewsItem> success) {
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
          setSuccess((List<NewsItem>)value);
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
      if (that instanceof getNewsItems_result)
        return this.equals((getNewsItems_result)that);
      return false;
    }

    public boolean equals(getNewsItems_result that) {
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
      return 0;
    }

    public int compareTo(getNewsItems_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;
      getNewsItems_result typedOther = (getNewsItems_result)other;

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
            if (field.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                this.success = new ArrayList<NewsItem>(_list8.size);
                for (int _i9 = 0; _i9 < _list8.size; ++_i9)
                {
                  NewsItem _elem10; // required
                  _elem10 = new NewsItem();
                  _elem10.read(iprot);
                  this.success.add(_elem10);
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
      oprot.writeStructBegin(STRUCT_DESC);

      if (this.isSetSuccess()) {
        oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, this.success.size()));
          for (NewsItem _iter11 : this.success)
          {
            _iter11.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("getNewsItems_result(");
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
