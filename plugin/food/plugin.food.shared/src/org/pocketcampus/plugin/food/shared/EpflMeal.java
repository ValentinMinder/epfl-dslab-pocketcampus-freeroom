/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.food.shared;

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

public class EpflMeal implements org.apache.thrift.TBase<EpflMeal, EpflMeal._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("EpflMeal");

  private static final org.apache.thrift.protocol.TField M_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("mId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField M_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("mName", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField M_DESCRIPTION_FIELD_DESC = new org.apache.thrift.protocol.TField("mDescription", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField M_PRICES_FIELD_DESC = new org.apache.thrift.protocol.TField("mPrices", org.apache.thrift.protocol.TType.MAP, (short)4);
  private static final org.apache.thrift.protocol.TField M_HALF_PORTION_PRICE_FIELD_DESC = new org.apache.thrift.protocol.TField("mHalfPortionPrice", org.apache.thrift.protocol.TType.DOUBLE, (short)5);
  private static final org.apache.thrift.protocol.TField M_TYPES_FIELD_DESC = new org.apache.thrift.protocol.TField("mTypes", org.apache.thrift.protocol.TType.LIST, (short)6);
  private static final org.apache.thrift.protocol.TField M_RATING_FIELD_DESC = new org.apache.thrift.protocol.TField("mRating", org.apache.thrift.protocol.TType.STRUCT, (short)7);

  private long mId; // required
  private String mName; // required
  private String mDescription; // required
  private Map<PriceTarget,Double> mPrices; // required
  private double mHalfPortionPrice; // required
  private List<MealType> mTypes; // required
  private EpflRating mRating; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    M_ID((short)1, "mId"),
    M_NAME((short)2, "mName"),
    M_DESCRIPTION((short)3, "mDescription"),
    M_PRICES((short)4, "mPrices"),
    M_HALF_PORTION_PRICE((short)5, "mHalfPortionPrice"),
    M_TYPES((short)6, "mTypes"),
    M_RATING((short)7, "mRating");

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
        case 1: // M_ID
          return M_ID;
        case 2: // M_NAME
          return M_NAME;
        case 3: // M_DESCRIPTION
          return M_DESCRIPTION;
        case 4: // M_PRICES
          return M_PRICES;
        case 5: // M_HALF_PORTION_PRICE
          return M_HALF_PORTION_PRICE;
        case 6: // M_TYPES
          return M_TYPES;
        case 7: // M_RATING
          return M_RATING;
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
  private static final int __MID_ISSET_ID = 0;
  private static final int __MHALFPORTIONPRICE_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.M_ID, new org.apache.thrift.meta_data.FieldMetaData("mId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.M_NAME, new org.apache.thrift.meta_data.FieldMetaData("mName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.M_DESCRIPTION, new org.apache.thrift.meta_data.FieldMetaData("mDescription", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.M_PRICES, new org.apache.thrift.meta_data.FieldMetaData("mPrices", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, PriceTarget.class), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE))));
    tmpMap.put(_Fields.M_HALF_PORTION_PRICE, new org.apache.thrift.meta_data.FieldMetaData("mHalfPortionPrice", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.M_TYPES, new org.apache.thrift.meta_data.FieldMetaData("mTypes", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, MealType.class))));
    tmpMap.put(_Fields.M_RATING, new org.apache.thrift.meta_data.FieldMetaData("mRating", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, EpflRating.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(EpflMeal.class, metaDataMap);
  }

  public EpflMeal() {
  }

  public EpflMeal(
    long mId,
    String mName,
    String mDescription,
    Map<PriceTarget,Double> mPrices,
    List<MealType> mTypes,
    EpflRating mRating)
  {
    this();
    this.mId = mId;
    setMIdIsSet(true);
    this.mName = mName;
    this.mDescription = mDescription;
    this.mPrices = mPrices;
    this.mTypes = mTypes;
    this.mRating = mRating;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public EpflMeal(EpflMeal other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.mId = other.mId;
    if (other.isSetMName()) {
      this.mName = other.mName;
    }
    if (other.isSetMDescription()) {
      this.mDescription = other.mDescription;
    }
    if (other.isSetMPrices()) {
      Map<PriceTarget,Double> __this__mPrices = new HashMap<PriceTarget,Double>();
      for (Map.Entry<PriceTarget, Double> other_element : other.mPrices.entrySet()) {

        PriceTarget other_element_key = other_element.getKey();
        Double other_element_value = other_element.getValue();

        PriceTarget __this__mPrices_copy_key = other_element_key;

        Double __this__mPrices_copy_value = other_element_value;

        __this__mPrices.put(__this__mPrices_copy_key, __this__mPrices_copy_value);
      }
      this.mPrices = __this__mPrices;
    }
    this.mHalfPortionPrice = other.mHalfPortionPrice;
    if (other.isSetMTypes()) {
      List<MealType> __this__mTypes = new ArrayList<MealType>();
      for (MealType other_element : other.mTypes) {
        __this__mTypes.add(other_element);
      }
      this.mTypes = __this__mTypes;
    }
    if (other.isSetMRating()) {
      this.mRating = new EpflRating(other.mRating);
    }
  }

  public EpflMeal deepCopy() {
    return new EpflMeal(this);
  }

  @Override
  public void clear() {
    setMIdIsSet(false);
    this.mId = 0;
    this.mName = null;
    this.mDescription = null;
    this.mPrices = null;
    setMHalfPortionPriceIsSet(false);
    this.mHalfPortionPrice = 0.0;
    this.mTypes = null;
    this.mRating = null;
  }

  public long getMId() {
    return this.mId;
  }

  public EpflMeal setMId(long mId) {
    this.mId = mId;
    setMIdIsSet(true);
    return this;
  }

  public void unsetMId() {
    __isset_bit_vector.clear(__MID_ISSET_ID);
  }

  /** Returns true if field mId is set (has been assigned a value) and false otherwise */
  public boolean isSetMId() {
    return __isset_bit_vector.get(__MID_ISSET_ID);
  }

  public void setMIdIsSet(boolean value) {
    __isset_bit_vector.set(__MID_ISSET_ID, value);
  }

  public String getMName() {
    return this.mName;
  }

  public EpflMeal setMName(String mName) {
    this.mName = mName;
    return this;
  }

  public void unsetMName() {
    this.mName = null;
  }

  /** Returns true if field mName is set (has been assigned a value) and false otherwise */
  public boolean isSetMName() {
    return this.mName != null;
  }

  public void setMNameIsSet(boolean value) {
    if (!value) {
      this.mName = null;
    }
  }

  public String getMDescription() {
    return this.mDescription;
  }

  public EpflMeal setMDescription(String mDescription) {
    this.mDescription = mDescription;
    return this;
  }

  public void unsetMDescription() {
    this.mDescription = null;
  }

  /** Returns true if field mDescription is set (has been assigned a value) and false otherwise */
  public boolean isSetMDescription() {
    return this.mDescription != null;
  }

  public void setMDescriptionIsSet(boolean value) {
    if (!value) {
      this.mDescription = null;
    }
  }

  public int getMPricesSize() {
    return (this.mPrices == null) ? 0 : this.mPrices.size();
  }

  public void putToMPrices(PriceTarget key, double val) {
    if (this.mPrices == null) {
      this.mPrices = new HashMap<PriceTarget,Double>();
    }
    this.mPrices.put(key, val);
  }

  public Map<PriceTarget,Double> getMPrices() {
    return this.mPrices;
  }

  public EpflMeal setMPrices(Map<PriceTarget,Double> mPrices) {
    this.mPrices = mPrices;
    return this;
  }

  public void unsetMPrices() {
    this.mPrices = null;
  }

  /** Returns true if field mPrices is set (has been assigned a value) and false otherwise */
  public boolean isSetMPrices() {
    return this.mPrices != null;
  }

  public void setMPricesIsSet(boolean value) {
    if (!value) {
      this.mPrices = null;
    }
  }

  public double getMHalfPortionPrice() {
    return this.mHalfPortionPrice;
  }

  public EpflMeal setMHalfPortionPrice(double mHalfPortionPrice) {
    this.mHalfPortionPrice = mHalfPortionPrice;
    setMHalfPortionPriceIsSet(true);
    return this;
  }

  public void unsetMHalfPortionPrice() {
    __isset_bit_vector.clear(__MHALFPORTIONPRICE_ISSET_ID);
  }

  /** Returns true if field mHalfPortionPrice is set (has been assigned a value) and false otherwise */
  public boolean isSetMHalfPortionPrice() {
    return __isset_bit_vector.get(__MHALFPORTIONPRICE_ISSET_ID);
  }

  public void setMHalfPortionPriceIsSet(boolean value) {
    __isset_bit_vector.set(__MHALFPORTIONPRICE_ISSET_ID, value);
  }

  public int getMTypesSize() {
    return (this.mTypes == null) ? 0 : this.mTypes.size();
  }

  public java.util.Iterator<MealType> getMTypesIterator() {
    return (this.mTypes == null) ? null : this.mTypes.iterator();
  }

  public void addToMTypes(MealType elem) {
    if (this.mTypes == null) {
      this.mTypes = new ArrayList<MealType>();
    }
    this.mTypes.add(elem);
  }

  public List<MealType> getMTypes() {
    return this.mTypes;
  }

  public EpflMeal setMTypes(List<MealType> mTypes) {
    this.mTypes = mTypes;
    return this;
  }

  public void unsetMTypes() {
    this.mTypes = null;
  }

  /** Returns true if field mTypes is set (has been assigned a value) and false otherwise */
  public boolean isSetMTypes() {
    return this.mTypes != null;
  }

  public void setMTypesIsSet(boolean value) {
    if (!value) {
      this.mTypes = null;
    }
  }

  public EpflRating getMRating() {
    return this.mRating;
  }

  public EpflMeal setMRating(EpflRating mRating) {
    this.mRating = mRating;
    return this;
  }

  public void unsetMRating() {
    this.mRating = null;
  }

  /** Returns true if field mRating is set (has been assigned a value) and false otherwise */
  public boolean isSetMRating() {
    return this.mRating != null;
  }

  public void setMRatingIsSet(boolean value) {
    if (!value) {
      this.mRating = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case M_ID:
      if (value == null) {
        unsetMId();
      } else {
        setMId((Long)value);
      }
      break;

    case M_NAME:
      if (value == null) {
        unsetMName();
      } else {
        setMName((String)value);
      }
      break;

    case M_DESCRIPTION:
      if (value == null) {
        unsetMDescription();
      } else {
        setMDescription((String)value);
      }
      break;

    case M_PRICES:
      if (value == null) {
        unsetMPrices();
      } else {
        setMPrices((Map<PriceTarget,Double>)value);
      }
      break;

    case M_HALF_PORTION_PRICE:
      if (value == null) {
        unsetMHalfPortionPrice();
      } else {
        setMHalfPortionPrice((Double)value);
      }
      break;

    case M_TYPES:
      if (value == null) {
        unsetMTypes();
      } else {
        setMTypes((List<MealType>)value);
      }
      break;

    case M_RATING:
      if (value == null) {
        unsetMRating();
      } else {
        setMRating((EpflRating)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case M_ID:
      return Long.valueOf(getMId());

    case M_NAME:
      return getMName();

    case M_DESCRIPTION:
      return getMDescription();

    case M_PRICES:
      return getMPrices();

    case M_HALF_PORTION_PRICE:
      return Double.valueOf(getMHalfPortionPrice());

    case M_TYPES:
      return getMTypes();

    case M_RATING:
      return getMRating();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case M_ID:
      return isSetMId();
    case M_NAME:
      return isSetMName();
    case M_DESCRIPTION:
      return isSetMDescription();
    case M_PRICES:
      return isSetMPrices();
    case M_HALF_PORTION_PRICE:
      return isSetMHalfPortionPrice();
    case M_TYPES:
      return isSetMTypes();
    case M_RATING:
      return isSetMRating();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof EpflMeal)
      return this.equals((EpflMeal)that);
    return false;
  }

  public boolean equals(EpflMeal that) {
    if (that == null)
      return false;

    boolean this_present_mId = true;
    boolean that_present_mId = true;
    if (this_present_mId || that_present_mId) {
      if (!(this_present_mId && that_present_mId))
        return false;
      if (this.mId != that.mId)
        return false;
    }

    boolean this_present_mName = true && this.isSetMName();
    boolean that_present_mName = true && that.isSetMName();
    if (this_present_mName || that_present_mName) {
      if (!(this_present_mName && that_present_mName))
        return false;
      if (!this.mName.equals(that.mName))
        return false;
    }

    boolean this_present_mDescription = true && this.isSetMDescription();
    boolean that_present_mDescription = true && that.isSetMDescription();
    if (this_present_mDescription || that_present_mDescription) {
      if (!(this_present_mDescription && that_present_mDescription))
        return false;
      if (!this.mDescription.equals(that.mDescription))
        return false;
    }

    boolean this_present_mPrices = true && this.isSetMPrices();
    boolean that_present_mPrices = true && that.isSetMPrices();
    if (this_present_mPrices || that_present_mPrices) {
      if (!(this_present_mPrices && that_present_mPrices))
        return false;
      if (!this.mPrices.equals(that.mPrices))
        return false;
    }

    boolean this_present_mHalfPortionPrice = true && this.isSetMHalfPortionPrice();
    boolean that_present_mHalfPortionPrice = true && that.isSetMHalfPortionPrice();
    if (this_present_mHalfPortionPrice || that_present_mHalfPortionPrice) {
      if (!(this_present_mHalfPortionPrice && that_present_mHalfPortionPrice))
        return false;
      if (this.mHalfPortionPrice != that.mHalfPortionPrice)
        return false;
    }

    boolean this_present_mTypes = true && this.isSetMTypes();
    boolean that_present_mTypes = true && that.isSetMTypes();
    if (this_present_mTypes || that_present_mTypes) {
      if (!(this_present_mTypes && that_present_mTypes))
        return false;
      if (!this.mTypes.equals(that.mTypes))
        return false;
    }

    boolean this_present_mRating = true && this.isSetMRating();
    boolean that_present_mRating = true && that.isSetMRating();
    if (this_present_mRating || that_present_mRating) {
      if (!(this_present_mRating && that_present_mRating))
        return false;
      if (!this.mRating.equals(that.mRating))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    boolean present_mId = true;
    builder.append(present_mId);
    if (present_mId)
      builder.append(mId);

    boolean present_mName = true && (isSetMName());
    builder.append(present_mName);
    if (present_mName)
      builder.append(mName);

    boolean present_mDescription = true && (isSetMDescription());
    builder.append(present_mDescription);
    if (present_mDescription)
      builder.append(mDescription);

    boolean present_mPrices = true && (isSetMPrices());
    builder.append(present_mPrices);
    if (present_mPrices)
      builder.append(mPrices);

    boolean present_mHalfPortionPrice = true && (isSetMHalfPortionPrice());
    builder.append(present_mHalfPortionPrice);
    if (present_mHalfPortionPrice)
      builder.append(mHalfPortionPrice);

    boolean present_mTypes = true && (isSetMTypes());
    builder.append(present_mTypes);
    if (present_mTypes)
      builder.append(mTypes);

    boolean present_mRating = true && (isSetMRating());
    builder.append(present_mRating);
    if (present_mRating)
      builder.append(mRating);

    return builder.toHashCode();
  }

  public int compareTo(EpflMeal other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    EpflMeal typedOther = (EpflMeal)other;

    lastComparison = Boolean.valueOf(isSetMId()).compareTo(typedOther.isSetMId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mId, typedOther.mId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMName()).compareTo(typedOther.isSetMName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mName, typedOther.mName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMDescription()).compareTo(typedOther.isSetMDescription());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMDescription()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mDescription, typedOther.mDescription);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMPrices()).compareTo(typedOther.isSetMPrices());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMPrices()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mPrices, typedOther.mPrices);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMHalfPortionPrice()).compareTo(typedOther.isSetMHalfPortionPrice());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMHalfPortionPrice()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mHalfPortionPrice, typedOther.mHalfPortionPrice);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMTypes()).compareTo(typedOther.isSetMTypes());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMTypes()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mTypes, typedOther.mTypes);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMRating()).compareTo(typedOther.isSetMRating());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMRating()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mRating, typedOther.mRating);
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
        case 1: // M_ID
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.mId = iprot.readI64();
            setMIdIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // M_NAME
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.mName = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // M_DESCRIPTION
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.mDescription = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // M_PRICES
          if (field.type == org.apache.thrift.protocol.TType.MAP) {
            {
              org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
              this.mPrices = new HashMap<PriceTarget,Double>(2*_map0.size);
              for (int _i1 = 0; _i1 < _map0.size; ++_i1)
              {
                PriceTarget _key2; // required
                double _val3; // required
                _key2 = PriceTarget.findByValue(iprot.readI32());
                _val3 = iprot.readDouble();
                this.mPrices.put(_key2, _val3);
              }
              iprot.readMapEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // M_HALF_PORTION_PRICE
          if (field.type == org.apache.thrift.protocol.TType.DOUBLE) {
            this.mHalfPortionPrice = iprot.readDouble();
            setMHalfPortionPriceIsSet(true);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 6: // M_TYPES
          if (field.type == org.apache.thrift.protocol.TType.LIST) {
            {
              org.apache.thrift.protocol.TList _list4 = iprot.readListBegin();
              this.mTypes = new ArrayList<MealType>(_list4.size);
              for (int _i5 = 0; _i5 < _list4.size; ++_i5)
              {
                MealType _elem6; // required
                _elem6 = MealType.findByValue(iprot.readI32());
                this.mTypes.add(_elem6);
              }
              iprot.readListEnd();
            }
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 7: // M_RATING
          if (field.type == org.apache.thrift.protocol.TType.STRUCT) {
            this.mRating = new EpflRating();
            this.mRating.read(iprot);
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
    if (!isSetMId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mId' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(M_ID_FIELD_DESC);
    oprot.writeI64(this.mId);
    oprot.writeFieldEnd();
    if (this.mName != null) {
      oprot.writeFieldBegin(M_NAME_FIELD_DESC);
      oprot.writeString(this.mName);
      oprot.writeFieldEnd();
    }
    if (this.mDescription != null) {
      oprot.writeFieldBegin(M_DESCRIPTION_FIELD_DESC);
      oprot.writeString(this.mDescription);
      oprot.writeFieldEnd();
    }
    if (this.mPrices != null) {
      oprot.writeFieldBegin(M_PRICES_FIELD_DESC);
      {
        oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.I32, org.apache.thrift.protocol.TType.DOUBLE, this.mPrices.size()));
        for (Map.Entry<PriceTarget, Double> _iter7 : this.mPrices.entrySet())
        {
          oprot.writeI32(_iter7.getKey().getValue());
          oprot.writeDouble(_iter7.getValue());
        }
        oprot.writeMapEnd();
      }
      oprot.writeFieldEnd();
    }
    if (isSetMHalfPortionPrice()) {
      oprot.writeFieldBegin(M_HALF_PORTION_PRICE_FIELD_DESC);
      oprot.writeDouble(this.mHalfPortionPrice);
      oprot.writeFieldEnd();
    }
    if (this.mTypes != null) {
      oprot.writeFieldBegin(M_TYPES_FIELD_DESC);
      {
        oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, this.mTypes.size()));
        for (MealType _iter8 : this.mTypes)
        {
          oprot.writeI32(_iter8.getValue());
        }
        oprot.writeListEnd();
      }
      oprot.writeFieldEnd();
    }
    if (this.mRating != null) {
      oprot.writeFieldBegin(M_RATING_FIELD_DESC);
      this.mRating.write(oprot);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("EpflMeal(");
    boolean first = true;

    sb.append("mId:");
    sb.append(this.mId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("mName:");
    if (this.mName == null) {
      sb.append("null");
    } else {
      sb.append(this.mName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("mDescription:");
    if (this.mDescription == null) {
      sb.append("null");
    } else {
      sb.append(this.mDescription);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("mPrices:");
    if (this.mPrices == null) {
      sb.append("null");
    } else {
      sb.append(this.mPrices);
    }
    first = false;
    if (isSetMHalfPortionPrice()) {
      if (!first) sb.append(", ");
      sb.append("mHalfPortionPrice:");
      sb.append(this.mHalfPortionPrice);
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("mTypes:");
    if (this.mTypes == null) {
      sb.append("null");
    } else {
      sb.append(this.mTypes);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("mRating:");
    if (this.mRating == null) {
      sb.append("null");
    } else {
      sb.append(this.mRating);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'mId' because it's a primitive and you chose the non-beans generator.
    if (mName == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mName' was not present! Struct: " + toString());
    }
    if (mDescription == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mDescription' was not present! Struct: " + toString());
    }
    if (mPrices == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mPrices' was not present! Struct: " + toString());
    }
    if (mTypes == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mTypes' was not present! Struct: " + toString());
    }
    if (mRating == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mRating' was not present! Struct: " + toString());
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

