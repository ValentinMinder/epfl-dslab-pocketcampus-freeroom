/**
 * Autogenerated by Thrift Compiler (0.7.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package org.pocketcampus.plugin.food.shared;

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

public class Meal implements org.apache.thrift.TBase<Meal, Meal._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Meal");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("Id", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField MEAL_DESCRIPTION_FIELD_DESC = new org.apache.thrift.protocol.TField("mealDescription", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField RESTAURANT_FIELD_DESC = new org.apache.thrift.protocol.TField("restaurant", org.apache.thrift.protocol.TType.STRUCT, (short)4);
  private static final org.apache.thrift.protocol.TField RATING_FIELD_DESC = new org.apache.thrift.protocol.TField("rating", org.apache.thrift.protocol.TType.STRUCT, (short)5);
  private static final org.apache.thrift.protocol.TField PRICE_FIELD_DESC = new org.apache.thrift.protocol.TField("price", org.apache.thrift.protocol.TType.DOUBLE, (short)6);

  public long Id; // required
  public String name; // required
  public String mealDescription; // required
  public Restaurant restaurant; // required
  public Rating rating; // required
  public double price; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "Id"),
    NAME((short)2, "name"),
    MEAL_DESCRIPTION((short)3, "mealDescription"),
    RESTAURANT((short)4, "restaurant"),
    RATING((short)5, "rating"),
    PRICE((short)6, "price");

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
        case 1: // ID
          return ID;
        case 2: // NAME
          return NAME;
        case 3: // MEAL_DESCRIPTION
          return MEAL_DESCRIPTION;
        case 4: // RESTAURANT
          return RESTAURANT;
        case 5: // RATING
          return RATING;
        case 6: // PRICE
          return PRICE;
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
  private static final int __ID_ISSET_ID = 0;
  private static final int __PRICE_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("Id", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64        , "Id")));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.MEAL_DESCRIPTION, new org.apache.thrift.meta_data.FieldMetaData("mealDescription", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RESTAURANT, new org.apache.thrift.meta_data.FieldMetaData("restaurant", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Restaurant.class)));
    tmpMap.put(_Fields.RATING, new org.apache.thrift.meta_data.FieldMetaData("rating", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Rating.class)));
    tmpMap.put(_Fields.PRICE, new org.apache.thrift.meta_data.FieldMetaData("price", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Meal.class, metaDataMap);
  }

  public Meal() {
  }

  public Meal(
    long Id,
    String name,
    String mealDescription,
    Restaurant restaurant,
    Rating rating)
  {
    this();
    this.Id = Id;
    setIdIsSet(true);
    this.name = name;
    this.mealDescription = mealDescription;
    this.restaurant = restaurant;
    this.rating = rating;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Meal(Meal other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.Id = other.Id;
    if (other.isSetName()) {
      this.name = other.name;
    }
    if (other.isSetMealDescription()) {
      this.mealDescription = other.mealDescription;
    }
    if (other.isSetRestaurant()) {
      this.restaurant = new Restaurant(other.restaurant);
    }
    if (other.isSetRating()) {
      this.rating = new Rating(other.rating);
    }
    this.price = other.price;
  }

  public Meal deepCopy() {
    return new Meal(this);
  }

  @Override
  public void clear() {
    setIdIsSet(false);
    this.Id = 0;
    this.name = null;
    this.mealDescription = null;
    this.restaurant = null;
    this.rating = null;
    setPriceIsSet(false);
    this.price = 0.0;
  }

  public long getId() {
    return this.Id;
  }

  public Meal setId(long Id) {
    this.Id = Id;
    setIdIsSet(true);
    return this;
  }

  public void unsetId() {
    __isset_bit_vector.clear(__ID_ISSET_ID);
  }

  /** Returns true if field Id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return __isset_bit_vector.get(__ID_ISSET_ID);
  }

  public void setIdIsSet(boolean value) {
    __isset_bit_vector.set(__ID_ISSET_ID, value);
  }

  public String getName() {
    return this.name;
  }

  public Meal setName(String name) {
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

  public String getMealDescription() {
    return this.mealDescription;
  }

  public Meal setMealDescription(String mealDescription) {
    this.mealDescription = mealDescription;
    return this;
  }

  public void unsetMealDescription() {
    this.mealDescription = null;
  }

  /** Returns true if field mealDescription is set (has been assigned a value) and false otherwise */
  public boolean isSetMealDescription() {
    return this.mealDescription != null;
  }

  public void setMealDescriptionIsSet(boolean value) {
    if (!value) {
      this.mealDescription = null;
    }
  }

  public Restaurant getRestaurant() {
    return this.restaurant;
  }

  public Meal setRestaurant(Restaurant restaurant) {
    this.restaurant = restaurant;
    return this;
  }

  public void unsetRestaurant() {
    this.restaurant = null;
  }

  /** Returns true if field restaurant is set (has been assigned a value) and false otherwise */
  public boolean isSetRestaurant() {
    return this.restaurant != null;
  }

  public void setRestaurantIsSet(boolean value) {
    if (!value) {
      this.restaurant = null;
    }
  }

  public Rating getRating() {
    return this.rating;
  }

  public Meal setRating(Rating rating) {
    this.rating = rating;
    return this;
  }

  public void unsetRating() {
    this.rating = null;
  }

  /** Returns true if field rating is set (has been assigned a value) and false otherwise */
  public boolean isSetRating() {
    return this.rating != null;
  }

  public void setRatingIsSet(boolean value) {
    if (!value) {
      this.rating = null;
    }
  }

  public double getPrice() {
    return this.price;
  }

  public Meal setPrice(double price) {
    this.price = price;
    setPriceIsSet(true);
    return this;
  }

  public void unsetPrice() {
    __isset_bit_vector.clear(__PRICE_ISSET_ID);
  }

  /** Returns true if field price is set (has been assigned a value) and false otherwise */
  public boolean isSetPrice() {
    return __isset_bit_vector.get(__PRICE_ISSET_ID);
  }

  public void setPriceIsSet(boolean value) {
    __isset_bit_vector.set(__PRICE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((Long)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case MEAL_DESCRIPTION:
      if (value == null) {
        unsetMealDescription();
      } else {
        setMealDescription((String)value);
      }
      break;

    case RESTAURANT:
      if (value == null) {
        unsetRestaurant();
      } else {
        setRestaurant((Restaurant)value);
      }
      break;

    case RATING:
      if (value == null) {
        unsetRating();
      } else {
        setRating((Rating)value);
      }
      break;

    case PRICE:
      if (value == null) {
        unsetPrice();
      } else {
        setPrice((Double)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return Long.valueOf(getId());

    case NAME:
      return getName();

    case MEAL_DESCRIPTION:
      return getMealDescription();

    case RESTAURANT:
      return getRestaurant();

    case RATING:
      return getRating();

    case PRICE:
      return Double.valueOf(getPrice());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case NAME:
      return isSetName();
    case MEAL_DESCRIPTION:
      return isSetMealDescription();
    case RESTAURANT:
      return isSetRestaurant();
    case RATING:
      return isSetRating();
    case PRICE:
      return isSetPrice();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Meal)
      return this.equals((Meal)that);
    return false;
  }

  public boolean equals(Meal that) {
    if (that == null)
      return false;

    boolean this_present_Id = true;
    boolean that_present_Id = true;
    if (this_present_Id || that_present_Id) {
      if (!(this_present_Id && that_present_Id))
        return false;
      if (this.Id != that.Id)
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

    boolean this_present_mealDescription = true && this.isSetMealDescription();
    boolean that_present_mealDescription = true && that.isSetMealDescription();
    if (this_present_mealDescription || that_present_mealDescription) {
      if (!(this_present_mealDescription && that_present_mealDescription))
        return false;
      if (!this.mealDescription.equals(that.mealDescription))
        return false;
    }

    boolean this_present_restaurant = true && this.isSetRestaurant();
    boolean that_present_restaurant = true && that.isSetRestaurant();
    if (this_present_restaurant || that_present_restaurant) {
      if (!(this_present_restaurant && that_present_restaurant))
        return false;
      if (!this.restaurant.equals(that.restaurant))
        return false;
    }

    boolean this_present_rating = true && this.isSetRating();
    boolean that_present_rating = true && that.isSetRating();
    if (this_present_rating || that_present_rating) {
      if (!(this_present_rating && that_present_rating))
        return false;
      if (!this.rating.equals(that.rating))
        return false;
    }

    boolean this_present_price = true && this.isSetPrice();
    boolean that_present_price = true && that.isSetPrice();
    if (this_present_price || that_present_price) {
      if (!(this_present_price && that_present_price))
        return false;
      if (this.price != that.price)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(Meal other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    Meal typedOther = (Meal)other;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(typedOther.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.Id, typedOther.Id);
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
    lastComparison = Boolean.valueOf(isSetMealDescription()).compareTo(typedOther.isSetMealDescription());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMealDescription()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mealDescription, typedOther.mealDescription);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRestaurant()).compareTo(typedOther.isSetRestaurant());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRestaurant()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.restaurant, typedOther.restaurant);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRating()).compareTo(typedOther.isSetRating());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRating()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.rating, typedOther.rating);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPrice()).compareTo(typedOther.isSetPrice());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPrice()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.price, typedOther.price);
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
        case 1: // ID
          if (field.type == org.apache.thrift.protocol.TType.I64) {
            this.Id = iprot.readI64();
            setIdIsSet(true);
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
        case 3: // MEAL_DESCRIPTION
          if (field.type == org.apache.thrift.protocol.TType.STRING) {
            this.mealDescription = iprot.readString();
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // RESTAURANT
          if (field.type == org.apache.thrift.protocol.TType.STRUCT) {
            this.restaurant = new Restaurant();
            this.restaurant.read(iprot);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // RATING
          if (field.type == org.apache.thrift.protocol.TType.STRUCT) {
            this.rating = new Rating();
            this.rating.read(iprot);
          } else { 
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 6: // PRICE
          if (field.type == org.apache.thrift.protocol.TType.DOUBLE) {
            this.price = iprot.readDouble();
            setPriceIsSet(true);
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
    if (!isSetId()) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'Id' was not found in serialized data! Struct: " + toString());
    }
    validate();
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    oprot.writeFieldBegin(ID_FIELD_DESC);
    oprot.writeI64(this.Id);
    oprot.writeFieldEnd();
    if (this.name != null) {
      oprot.writeFieldBegin(NAME_FIELD_DESC);
      oprot.writeString(this.name);
      oprot.writeFieldEnd();
    }
    if (this.mealDescription != null) {
      oprot.writeFieldBegin(MEAL_DESCRIPTION_FIELD_DESC);
      oprot.writeString(this.mealDescription);
      oprot.writeFieldEnd();
    }
    if (this.restaurant != null) {
      oprot.writeFieldBegin(RESTAURANT_FIELD_DESC);
      this.restaurant.write(oprot);
      oprot.writeFieldEnd();
    }
    if (this.rating != null) {
      oprot.writeFieldBegin(RATING_FIELD_DESC);
      this.rating.write(oprot);
      oprot.writeFieldEnd();
    }
    if (isSetPrice()) {
      oprot.writeFieldBegin(PRICE_FIELD_DESC);
      oprot.writeDouble(this.price);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Meal(");
    boolean first = true;

    sb.append("Id:");
    sb.append(this.Id);
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
    sb.append("mealDescription:");
    if (this.mealDescription == null) {
      sb.append("null");
    } else {
      sb.append(this.mealDescription);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("restaurant:");
    if (this.restaurant == null) {
      sb.append("null");
    } else {
      sb.append(this.restaurant);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("rating:");
    if (this.rating == null) {
      sb.append("null");
    } else {
      sb.append(this.rating);
    }
    first = false;
    if (isSetPrice()) {
      if (!first) sb.append(", ");
      sb.append("price:");
      sb.append(this.price);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'Id' because it's a primitive and you chose the non-beans generator.
    if (name == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'name' was not present! Struct: " + toString());
    }
    if (mealDescription == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'mealDescription' was not present! Struct: " + toString());
    }
    if (restaurant == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'restaurant' was not present! Struct: " + toString());
    }
    if (rating == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'rating' was not present! Struct: " + toString());
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

