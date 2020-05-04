package uk.gov.ons.ctp.response.action.message.instruction;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * Java class for ActionAddress complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionAddress"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estabType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="locality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="organisationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="line1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="line2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="townName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="postcode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ladCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="latitude" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="longitude" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionAddress",
    propOrder = {
      "type",
      "estabType",
      "locality",
      "organisationName",
      "category",
      "line1",
      "line2",
      "townName",
      "postcode",
      "country",
      "ladCode",
      "latitude",
      "longitude"
    })
public class ActionAddress implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;
  protected String type;
  protected String estabType;
  protected String locality;
  protected String organisationName;
  protected String category;
  protected String line1;
  protected String line2;
  protected String townName;
  protected String postcode;
  protected String country;
  protected String ladCode;
  protected BigDecimal latitude;
  protected BigDecimal longitude;

  /** Default no-arg constructor */
  public ActionAddress() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionAddress(
      final String type,
      final String estabType,
      final String locality,
      final String organisationName,
      final String category,
      final String line1,
      final String line2,
      final String townName,
      final String postcode,
      final String country,
      final String ladCode,
      final BigDecimal latitude,
      final BigDecimal longitude) {
    this.type = type;
    this.estabType = estabType;
    this.locality = locality;
    this.organisationName = organisationName;
    this.category = category;
    this.line1 = line1;
    this.line2 = line2;
    this.townName = townName;
    this.postcode = postcode;
    this.country = country;
    this.ladCode = ladCode;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Gets the value of the type property.
   *
   * @return possible object is {@link String }
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of the type property.
   *
   * @param value allowed object is {@link String }
   */
  public void setType(String value) {
    this.type = value;
  }

  /**
   * Gets the value of the estabType property.
   *
   * @return possible object is {@link String }
   */
  public String getEstabType() {
    return estabType;
  }

  /**
   * Sets the value of the estabType property.
   *
   * @param value allowed object is {@link String }
   */
  public void setEstabType(String value) {
    this.estabType = value;
  }

  /**
   * Gets the value of the locality property.
   *
   * @return possible object is {@link String }
   */
  public String getLocality() {
    return locality;
  }

  /**
   * Sets the value of the locality property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLocality(String value) {
    this.locality = value;
  }

  /**
   * Gets the value of the organisationName property.
   *
   * @return possible object is {@link String }
   */
  public String getOrganisationName() {
    return organisationName;
  }

  /**
   * Sets the value of the organisationName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setOrganisationName(String value) {
    this.organisationName = value;
  }

  /**
   * Gets the value of the category property.
   *
   * @return possible object is {@link String }
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the value of the category property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCategory(String value) {
    this.category = value;
  }

  /**
   * Gets the value of the line1 property.
   *
   * @return possible object is {@link String }
   */
  public String getLine1() {
    return line1;
  }

  /**
   * Sets the value of the line1 property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLine1(String value) {
    this.line1 = value;
  }

  /**
   * Gets the value of the line2 property.
   *
   * @return possible object is {@link String }
   */
  public String getLine2() {
    return line2;
  }

  /**
   * Sets the value of the line2 property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLine2(String value) {
    this.line2 = value;
  }

  /**
   * Gets the value of the townName property.
   *
   * @return possible object is {@link String }
   */
  public String getTownName() {
    return townName;
  }

  /**
   * Sets the value of the townName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTownName(String value) {
    this.townName = value;
  }

  /**
   * Gets the value of the postcode property.
   *
   * @return possible object is {@link String }
   */
  public String getPostcode() {
    return postcode;
  }

  /**
   * Sets the value of the postcode property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPostcode(String value) {
    this.postcode = value;
  }

  /**
   * Gets the value of the country property.
   *
   * @return possible object is {@link String }
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the value of the country property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCountry(String value) {
    this.country = value;
  }

  /**
   * Gets the value of the ladCode property.
   *
   * @return possible object is {@link String }
   */
  public String getLadCode() {
    return ladCode;
  }

  /**
   * Sets the value of the ladCode property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLadCode(String value) {
    this.ladCode = value;
  }

  /**
   * Gets the value of the latitude property.
   *
   * @return possible object is {@link BigDecimal }
   */
  public BigDecimal getLatitude() {
    return latitude;
  }

  /**
   * Sets the value of the latitude property.
   *
   * @param value allowed object is {@link BigDecimal }
   */
  public void setLatitude(BigDecimal value) {
    this.latitude = value;
  }

  /**
   * Gets the value of the longitude property.
   *
   * @return possible object is {@link BigDecimal }
   */
  public BigDecimal getLongitude() {
    return longitude;
  }

  /**
   * Sets the value of the longitude property.
   *
   * @param value allowed object is {@link BigDecimal }
   */
  public void setLongitude(BigDecimal value) {
    this.longitude = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionAddress.Builder<_B> _other) {
    _other.type = this.type;
    _other.estabType = this.estabType;
    _other.locality = this.locality;
    _other.organisationName = this.organisationName;
    _other.category = this.category;
    _other.line1 = this.line1;
    _other.line2 = this.line2;
    _other.townName = this.townName;
    _other.postcode = this.postcode;
    _other.country = this.country;
    _other.ladCode = this.ladCode;
    _other.latitude = this.latitude;
    _other.longitude = this.longitude;
  }

  public <_B> ActionAddress.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionAddress.Builder<_B>(_parentBuilder, this, true);
  }

  public ActionAddress.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionAddress.Builder<Void> builder() {
    return new ActionAddress.Builder<Void>(null, null, false);
  }

  public static <_B> ActionAddress.Builder<_B> copyOf(final ActionAddress _other) {
    final ActionAddress.Builder<_B> _newBuilder = new ActionAddress.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder);
    return _newBuilder;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(
      final ActionAddress.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree typePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("type"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (typePropertyTree != null)
        : ((typePropertyTree == null) || (!typePropertyTree.isLeaf())))) {
      _other.type = this.type;
    }
    final PropertyTree estabTypePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("estabType"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (estabTypePropertyTree != null)
        : ((estabTypePropertyTree == null) || (!estabTypePropertyTree.isLeaf())))) {
      _other.estabType = this.estabType;
    }
    final PropertyTree localityPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("locality"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (localityPropertyTree != null)
        : ((localityPropertyTree == null) || (!localityPropertyTree.isLeaf())))) {
      _other.locality = this.locality;
    }
    final PropertyTree organisationNamePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("organisationName"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (organisationNamePropertyTree != null)
        : ((organisationNamePropertyTree == null) || (!organisationNamePropertyTree.isLeaf())))) {
      _other.organisationName = this.organisationName;
    }
    final PropertyTree categoryPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("category"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (categoryPropertyTree != null)
        : ((categoryPropertyTree == null) || (!categoryPropertyTree.isLeaf())))) {
      _other.category = this.category;
    }
    final PropertyTree line1PropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("line1"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (line1PropertyTree != null)
        : ((line1PropertyTree == null) || (!line1PropertyTree.isLeaf())))) {
      _other.line1 = this.line1;
    }
    final PropertyTree line2PropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("line2"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (line2PropertyTree != null)
        : ((line2PropertyTree == null) || (!line2PropertyTree.isLeaf())))) {
      _other.line2 = this.line2;
    }
    final PropertyTree townNamePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("townName"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (townNamePropertyTree != null)
        : ((townNamePropertyTree == null) || (!townNamePropertyTree.isLeaf())))) {
      _other.townName = this.townName;
    }
    final PropertyTree postcodePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("postcode"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (postcodePropertyTree != null)
        : ((postcodePropertyTree == null) || (!postcodePropertyTree.isLeaf())))) {
      _other.postcode = this.postcode;
    }
    final PropertyTree countryPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("country"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (countryPropertyTree != null)
        : ((countryPropertyTree == null) || (!countryPropertyTree.isLeaf())))) {
      _other.country = this.country;
    }
    final PropertyTree ladCodePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("ladCode"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (ladCodePropertyTree != null)
        : ((ladCodePropertyTree == null) || (!ladCodePropertyTree.isLeaf())))) {
      _other.ladCode = this.ladCode;
    }
    final PropertyTree latitudePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("latitude"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (latitudePropertyTree != null)
        : ((latitudePropertyTree == null) || (!latitudePropertyTree.isLeaf())))) {
      _other.latitude = this.latitude;
    }
    final PropertyTree longitudePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("longitude"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (longitudePropertyTree != null)
        : ((longitudePropertyTree == null) || (!longitudePropertyTree.isLeaf())))) {
      _other.longitude = this.longitude;
    }
  }

  public <_B> ActionAddress.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionAddress.Builder<_B>(
        _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public ActionAddress.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionAddress.Builder<_B> copyOf(
      final ActionAddress _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionAddress.Builder<_B> _newBuilder = new ActionAddress.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionAddress.Builder<Void> copyExcept(
      final ActionAddress _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionAddress.Builder<Void> copyOnly(
      final ActionAddress _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public boolean equals(Object object) {
    if ((object == null) || (this.getClass() != object.getClass())) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final ActionAddress that = ((ActionAddress) object);
    {
      String leftType;
      leftType = this.getType();
      String rightType;
      rightType = that.getType();
      if (this.type != null) {
        if (that.type != null) {
          if (!leftType.equals(rightType)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.type != null) {
          return false;
        }
      }
    }
    {
      String leftEstabType;
      leftEstabType = this.getEstabType();
      String rightEstabType;
      rightEstabType = that.getEstabType();
      if (this.estabType != null) {
        if (that.estabType != null) {
          if (!leftEstabType.equals(rightEstabType)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.estabType != null) {
          return false;
        }
      }
    }
    {
      String leftLocality;
      leftLocality = this.getLocality();
      String rightLocality;
      rightLocality = that.getLocality();
      if (this.locality != null) {
        if (that.locality != null) {
          if (!leftLocality.equals(rightLocality)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.locality != null) {
          return false;
        }
      }
    }
    {
      String leftOrganisationName;
      leftOrganisationName = this.getOrganisationName();
      String rightOrganisationName;
      rightOrganisationName = that.getOrganisationName();
      if (this.organisationName != null) {
        if (that.organisationName != null) {
          if (!leftOrganisationName.equals(rightOrganisationName)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.organisationName != null) {
          return false;
        }
      }
    }
    {
      String leftCategory;
      leftCategory = this.getCategory();
      String rightCategory;
      rightCategory = that.getCategory();
      if (this.category != null) {
        if (that.category != null) {
          if (!leftCategory.equals(rightCategory)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.category != null) {
          return false;
        }
      }
    }
    {
      String leftLine1;
      leftLine1 = this.getLine1();
      String rightLine1;
      rightLine1 = that.getLine1();
      if (this.line1 != null) {
        if (that.line1 != null) {
          if (!leftLine1.equals(rightLine1)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.line1 != null) {
          return false;
        }
      }
    }
    {
      String leftLine2;
      leftLine2 = this.getLine2();
      String rightLine2;
      rightLine2 = that.getLine2();
      if (this.line2 != null) {
        if (that.line2 != null) {
          if (!leftLine2.equals(rightLine2)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.line2 != null) {
          return false;
        }
      }
    }
    {
      String leftTownName;
      leftTownName = this.getTownName();
      String rightTownName;
      rightTownName = that.getTownName();
      if (this.townName != null) {
        if (that.townName != null) {
          if (!leftTownName.equals(rightTownName)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.townName != null) {
          return false;
        }
      }
    }
    {
      String leftPostcode;
      leftPostcode = this.getPostcode();
      String rightPostcode;
      rightPostcode = that.getPostcode();
      if (this.postcode != null) {
        if (that.postcode != null) {
          if (!leftPostcode.equals(rightPostcode)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.postcode != null) {
          return false;
        }
      }
    }
    {
      String leftCountry;
      leftCountry = this.getCountry();
      String rightCountry;
      rightCountry = that.getCountry();
      if (this.country != null) {
        if (that.country != null) {
          if (!leftCountry.equals(rightCountry)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.country != null) {
          return false;
        }
      }
    }
    {
      String leftLadCode;
      leftLadCode = this.getLadCode();
      String rightLadCode;
      rightLadCode = that.getLadCode();
      if (this.ladCode != null) {
        if (that.ladCode != null) {
          if (!leftLadCode.equals(rightLadCode)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.ladCode != null) {
          return false;
        }
      }
    }
    {
      BigDecimal leftLatitude;
      leftLatitude = this.getLatitude();
      BigDecimal rightLatitude;
      rightLatitude = that.getLatitude();
      if (this.latitude != null) {
        if (that.latitude != null) {
          if (!leftLatitude.equals(rightLatitude)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.latitude != null) {
          return false;
        }
      }
    }
    {
      BigDecimal leftLongitude;
      leftLongitude = this.getLongitude();
      BigDecimal rightLongitude;
      rightLongitude = that.getLongitude();
      if (this.longitude != null) {
        if (that.longitude != null) {
          if (!leftLongitude.equals(rightLongitude)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.longitude != null) {
          return false;
        }
      }
    }
    return true;
  }

  public int hashCode() {
    int currentHashCode = 1;
    {
      currentHashCode = (currentHashCode * 31);
      String theType;
      theType = this.getType();
      if (this.type != null) {
        currentHashCode += theType.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theEstabType;
      theEstabType = this.getEstabType();
      if (this.estabType != null) {
        currentHashCode += theEstabType.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theLocality;
      theLocality = this.getLocality();
      if (this.locality != null) {
        currentHashCode += theLocality.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theOrganisationName;
      theOrganisationName = this.getOrganisationName();
      if (this.organisationName != null) {
        currentHashCode += theOrganisationName.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theCategory;
      theCategory = this.getCategory();
      if (this.category != null) {
        currentHashCode += theCategory.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theLine1;
      theLine1 = this.getLine1();
      if (this.line1 != null) {
        currentHashCode += theLine1.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theLine2;
      theLine2 = this.getLine2();
      if (this.line2 != null) {
        currentHashCode += theLine2.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theTownName;
      theTownName = this.getTownName();
      if (this.townName != null) {
        currentHashCode += theTownName.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String thePostcode;
      thePostcode = this.getPostcode();
      if (this.postcode != null) {
        currentHashCode += thePostcode.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theCountry;
      theCountry = this.getCountry();
      if (this.country != null) {
        currentHashCode += theCountry.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theLadCode;
      theLadCode = this.getLadCode();
      if (this.ladCode != null) {
        currentHashCode += theLadCode.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      BigDecimal theLatitude;
      theLatitude = this.getLatitude();
      if (this.latitude != null) {
        currentHashCode += theLatitude.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      BigDecimal theLongitude;
      theLongitude = this.getLongitude();
      if (this.longitude != null) {
        currentHashCode += theLongitude.hashCode();
      }
    }
    return currentHashCode;
  }

  public String toString() {
    final ToStringStrategy2 strategy = JAXBToStringStrategy.INSTANCE;
    final StringBuilder buffer = new StringBuilder();
    append(null, buffer, strategy);
    return buffer.toString();
  }

  public StringBuilder append(
      ObjectLocator locator, StringBuilder buffer, ToStringStrategy2 strategy) {
    strategy.appendStart(locator, this, buffer);
    appendFields(locator, buffer, strategy);
    strategy.appendEnd(locator, this, buffer);
    return buffer;
  }

  public StringBuilder appendFields(
      ObjectLocator locator, StringBuilder buffer, ToStringStrategy2 strategy) {
    {
      String theType;
      theType = this.getType();
      strategy.appendField(locator, this, "type", buffer, theType, (this.type != null));
    }
    {
      String theEstabType;
      theEstabType = this.getEstabType();
      strategy.appendField(
          locator, this, "estabType", buffer, theEstabType, (this.estabType != null));
    }
    {
      String theLocality;
      theLocality = this.getLocality();
      strategy.appendField(locator, this, "locality", buffer, theLocality, (this.locality != null));
    }
    {
      String theOrganisationName;
      theOrganisationName = this.getOrganisationName();
      strategy.appendField(
          locator,
          this,
          "organisationName",
          buffer,
          theOrganisationName,
          (this.organisationName != null));
    }
    {
      String theCategory;
      theCategory = this.getCategory();
      strategy.appendField(locator, this, "category", buffer, theCategory, (this.category != null));
    }
    {
      String theLine1;
      theLine1 = this.getLine1();
      strategy.appendField(locator, this, "line1", buffer, theLine1, (this.line1 != null));
    }
    {
      String theLine2;
      theLine2 = this.getLine2();
      strategy.appendField(locator, this, "line2", buffer, theLine2, (this.line2 != null));
    }
    {
      String theTownName;
      theTownName = this.getTownName();
      strategy.appendField(locator, this, "townName", buffer, theTownName, (this.townName != null));
    }
    {
      String thePostcode;
      thePostcode = this.getPostcode();
      strategy.appendField(locator, this, "postcode", buffer, thePostcode, (this.postcode != null));
    }
    {
      String theCountry;
      theCountry = this.getCountry();
      strategy.appendField(locator, this, "country", buffer, theCountry, (this.country != null));
    }
    {
      String theLadCode;
      theLadCode = this.getLadCode();
      strategy.appendField(locator, this, "ladCode", buffer, theLadCode, (this.ladCode != null));
    }
    {
      BigDecimal theLatitude;
      theLatitude = this.getLatitude();
      strategy.appendField(locator, this, "latitude", buffer, theLatitude, (this.latitude != null));
    }
    {
      BigDecimal theLongitude;
      theLongitude = this.getLongitude();
      strategy.appendField(
          locator, this, "longitude", buffer, theLongitude, (this.longitude != null));
    }
    return buffer;
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final ActionAddress _storedValue;
    private String type;
    private String estabType;
    private String locality;
    private String organisationName;
    private String category;
    private String line1;
    private String line2;
    private String townName;
    private String postcode;
    private String country;
    private String ladCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Builder(final _B _parentBuilder, final ActionAddress _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.type = _other.type;
          this.estabType = _other.estabType;
          this.locality = _other.locality;
          this.organisationName = _other.organisationName;
          this.category = _other.category;
          this.line1 = _other.line1;
          this.line2 = _other.line2;
          this.townName = _other.townName;
          this.postcode = _other.postcode;
          this.country = _other.country;
          this.ladCode = _other.ladCode;
          this.latitude = _other.latitude;
          this.longitude = _other.longitude;
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionAddress _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree typePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("type"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (typePropertyTree != null)
              : ((typePropertyTree == null) || (!typePropertyTree.isLeaf())))) {
            this.type = _other.type;
          }
          final PropertyTree estabTypePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("estabType"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (estabTypePropertyTree != null)
              : ((estabTypePropertyTree == null) || (!estabTypePropertyTree.isLeaf())))) {
            this.estabType = _other.estabType;
          }
          final PropertyTree localityPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("locality"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (localityPropertyTree != null)
              : ((localityPropertyTree == null) || (!localityPropertyTree.isLeaf())))) {
            this.locality = _other.locality;
          }
          final PropertyTree organisationNamePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("organisationName"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (organisationNamePropertyTree != null)
              : ((organisationNamePropertyTree == null)
                  || (!organisationNamePropertyTree.isLeaf())))) {
            this.organisationName = _other.organisationName;
          }
          final PropertyTree categoryPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("category"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (categoryPropertyTree != null)
              : ((categoryPropertyTree == null) || (!categoryPropertyTree.isLeaf())))) {
            this.category = _other.category;
          }
          final PropertyTree line1PropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("line1"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (line1PropertyTree != null)
              : ((line1PropertyTree == null) || (!line1PropertyTree.isLeaf())))) {
            this.line1 = _other.line1;
          }
          final PropertyTree line2PropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("line2"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (line2PropertyTree != null)
              : ((line2PropertyTree == null) || (!line2PropertyTree.isLeaf())))) {
            this.line2 = _other.line2;
          }
          final PropertyTree townNamePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("townName"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (townNamePropertyTree != null)
              : ((townNamePropertyTree == null) || (!townNamePropertyTree.isLeaf())))) {
            this.townName = _other.townName;
          }
          final PropertyTree postcodePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("postcode"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (postcodePropertyTree != null)
              : ((postcodePropertyTree == null) || (!postcodePropertyTree.isLeaf())))) {
            this.postcode = _other.postcode;
          }
          final PropertyTree countryPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("country"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (countryPropertyTree != null)
              : ((countryPropertyTree == null) || (!countryPropertyTree.isLeaf())))) {
            this.country = _other.country;
          }
          final PropertyTree ladCodePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("ladCode"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (ladCodePropertyTree != null)
              : ((ladCodePropertyTree == null) || (!ladCodePropertyTree.isLeaf())))) {
            this.ladCode = _other.ladCode;
          }
          final PropertyTree latitudePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("latitude"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (latitudePropertyTree != null)
              : ((latitudePropertyTree == null) || (!latitudePropertyTree.isLeaf())))) {
            this.latitude = _other.latitude;
          }
          final PropertyTree longitudePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("longitude"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (longitudePropertyTree != null)
              : ((longitudePropertyTree == null) || (!longitudePropertyTree.isLeaf())))) {
            this.longitude = _other.longitude;
          }
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public _B end() {
      return this._parentBuilder;
    }

    protected <_P extends ActionAddress> _P init(final _P _product) {
      _product.type = this.type;
      _product.estabType = this.estabType;
      _product.locality = this.locality;
      _product.organisationName = this.organisationName;
      _product.category = this.category;
      _product.line1 = this.line1;
      _product.line2 = this.line2;
      _product.townName = this.townName;
      _product.postcode = this.postcode;
      _product.country = this.country;
      _product.ladCode = this.ladCode;
      _product.latitude = this.latitude;
      _product.longitude = this.longitude;
      return _product;
    }

    /**
     * Sets the new value of "type" (any previous value will be replaced)
     *
     * @param type New value of the "type" property.
     */
    public ActionAddress.Builder<_B> withType(final String type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the new value of "estabType" (any previous value will be replaced)
     *
     * @param estabType New value of the "estabType" property.
     */
    public ActionAddress.Builder<_B> withEstabType(final String estabType) {
      this.estabType = estabType;
      return this;
    }

    /**
     * Sets the new value of "locality" (any previous value will be replaced)
     *
     * @param locality New value of the "locality" property.
     */
    public ActionAddress.Builder<_B> withLocality(final String locality) {
      this.locality = locality;
      return this;
    }

    /**
     * Sets the new value of "organisationName" (any previous value will be replaced)
     *
     * @param organisationName New value of the "organisationName" property.
     */
    public ActionAddress.Builder<_B> withOrganisationName(final String organisationName) {
      this.organisationName = organisationName;
      return this;
    }

    /**
     * Sets the new value of "category" (any previous value will be replaced)
     *
     * @param category New value of the "category" property.
     */
    public ActionAddress.Builder<_B> withCategory(final String category) {
      this.category = category;
      return this;
    }

    /**
     * Sets the new value of "line1" (any previous value will be replaced)
     *
     * @param line1 New value of the "line1" property.
     */
    public ActionAddress.Builder<_B> withLine1(final String line1) {
      this.line1 = line1;
      return this;
    }

    /**
     * Sets the new value of "line2" (any previous value will be replaced)
     *
     * @param line2 New value of the "line2" property.
     */
    public ActionAddress.Builder<_B> withLine2(final String line2) {
      this.line2 = line2;
      return this;
    }

    /**
     * Sets the new value of "townName" (any previous value will be replaced)
     *
     * @param townName New value of the "townName" property.
     */
    public ActionAddress.Builder<_B> withTownName(final String townName) {
      this.townName = townName;
      return this;
    }

    /**
     * Sets the new value of "postcode" (any previous value will be replaced)
     *
     * @param postcode New value of the "postcode" property.
     */
    public ActionAddress.Builder<_B> withPostcode(final String postcode) {
      this.postcode = postcode;
      return this;
    }

    /**
     * Sets the new value of "country" (any previous value will be replaced)
     *
     * @param country New value of the "country" property.
     */
    public ActionAddress.Builder<_B> withCountry(final String country) {
      this.country = country;
      return this;
    }

    /**
     * Sets the new value of "ladCode" (any previous value will be replaced)
     *
     * @param ladCode New value of the "ladCode" property.
     */
    public ActionAddress.Builder<_B> withLadCode(final String ladCode) {
      this.ladCode = ladCode;
      return this;
    }

    /**
     * Sets the new value of "latitude" (any previous value will be replaced)
     *
     * @param latitude New value of the "latitude" property.
     */
    public ActionAddress.Builder<_B> withLatitude(final BigDecimal latitude) {
      this.latitude = latitude;
      return this;
    }

    /**
     * Sets the new value of "longitude" (any previous value will be replaced)
     *
     * @param longitude New value of the "longitude" property.
     */
    public ActionAddress.Builder<_B> withLongitude(final BigDecimal longitude) {
      this.longitude = longitude;
      return this;
    }

    @Override
    public ActionAddress build() {
      if (_storedValue == null) {
        return this.init(new ActionAddress());
      } else {
        return ((ActionAddress) _storedValue);
      }
    }
  }

  public static class Select extends ActionAddress.Selector<ActionAddress.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionAddress.Select _root() {
      return new ActionAddress.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> type = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> estabType =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> locality =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>
        organisationName = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> category =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> line1 = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> line2 = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> townName =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> postcode =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> country =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> ladCode =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> latitude =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> longitude =
        null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.type != null) {
        products.put("type", this.type.init());
      }
      if (this.estabType != null) {
        products.put("estabType", this.estabType.init());
      }
      if (this.locality != null) {
        products.put("locality", this.locality.init());
      }
      if (this.organisationName != null) {
        products.put("organisationName", this.organisationName.init());
      }
      if (this.category != null) {
        products.put("category", this.category.init());
      }
      if (this.line1 != null) {
        products.put("line1", this.line1.init());
      }
      if (this.line2 != null) {
        products.put("line2", this.line2.init());
      }
      if (this.townName != null) {
        products.put("townName", this.townName.init());
      }
      if (this.postcode != null) {
        products.put("postcode", this.postcode.init());
      }
      if (this.country != null) {
        products.put("country", this.country.init());
      }
      if (this.ladCode != null) {
        products.put("ladCode", this.ladCode.init());
      }
      if (this.latitude != null) {
        products.put("latitude", this.latitude.init());
      }
      if (this.longitude != null) {
        products.put("longitude", this.longitude.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> type() {
      return ((this.type == null)
          ? this.type =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "type")
          : this.type);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> estabType() {
      return ((this.estabType == null)
          ? this.estabType =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "estabType")
          : this.estabType);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> locality() {
      return ((this.locality == null)
          ? this.locality =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "locality")
          : this.locality);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>
        organisationName() {
      return ((this.organisationName == null)
          ? this.organisationName =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "organisationName")
          : this.organisationName);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> category() {
      return ((this.category == null)
          ? this.category =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "category")
          : this.category);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> line1() {
      return ((this.line1 == null)
          ? this.line1 =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "line1")
          : this.line1);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> line2() {
      return ((this.line2 == null)
          ? this.line2 =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "line2")
          : this.line2);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> townName() {
      return ((this.townName == null)
          ? this.townName =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "townName")
          : this.townName);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> postcode() {
      return ((this.postcode == null)
          ? this.postcode =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "postcode")
          : this.postcode);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> country() {
      return ((this.country == null)
          ? this.country =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "country")
          : this.country);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> ladCode() {
      return ((this.ladCode == null)
          ? this.ladCode =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "ladCode")
          : this.ladCode);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> latitude() {
      return ((this.latitude == null)
          ? this.latitude =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "latitude")
          : this.latitude);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>> longitude() {
      return ((this.longitude == null)
          ? this.longitude =
              new com.kscs.util.jaxb.Selector<TRoot, ActionAddress.Selector<TRoot, TParent>>(
                  this._root, this, "longitude")
          : this.longitude);
    }
  }
}
