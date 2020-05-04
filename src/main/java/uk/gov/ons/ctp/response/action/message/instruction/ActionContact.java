package uk.gov.ons.ctp.response.action.message.instruction;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.io.Serializable;
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
 * Java class for ActionContact complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionContact"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="forename" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="phoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="emailAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ruName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tradingStyle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionContact",
    propOrder = {
      "title",
      "forename",
      "surname",
      "phoneNumber",
      "emailAddress",
      "ruName",
      "tradingStyle"
    })
public class ActionContact implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;
  protected String title;
  protected String forename;
  protected String surname;
  protected String phoneNumber;
  protected String emailAddress;
  protected String ruName;
  protected String tradingStyle;

  /** Default no-arg constructor */
  public ActionContact() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionContact(
      final String title,
      final String forename,
      final String surname,
      final String phoneNumber,
      final String emailAddress,
      final String ruName,
      final String tradingStyle) {
    this.title = title;
    this.forename = forename;
    this.surname = surname;
    this.phoneNumber = phoneNumber;
    this.emailAddress = emailAddress;
    this.ruName = ruName;
    this.tradingStyle = tradingStyle;
  }

  /**
   * Gets the value of the title property.
   *
   * @return possible object is {@link String }
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the value of the title property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTitle(String value) {
    this.title = value;
  }

  /**
   * Gets the value of the forename property.
   *
   * @return possible object is {@link String }
   */
  public String getForename() {
    return forename;
  }

  /**
   * Sets the value of the forename property.
   *
   * @param value allowed object is {@link String }
   */
  public void setForename(String value) {
    this.forename = value;
  }

  /**
   * Gets the value of the surname property.
   *
   * @return possible object is {@link String }
   */
  public String getSurname() {
    return surname;
  }

  /**
   * Sets the value of the surname property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSurname(String value) {
    this.surname = value;
  }

  /**
   * Gets the value of the phoneNumber property.
   *
   * @return possible object is {@link String }
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the value of the phoneNumber property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPhoneNumber(String value) {
    this.phoneNumber = value;
  }

  /**
   * Gets the value of the emailAddress property.
   *
   * @return possible object is {@link String }
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   * Sets the value of the emailAddress property.
   *
   * @param value allowed object is {@link String }
   */
  public void setEmailAddress(String value) {
    this.emailAddress = value;
  }

  /**
   * Gets the value of the ruName property.
   *
   * @return possible object is {@link String }
   */
  public String getRuName() {
    return ruName;
  }

  /**
   * Sets the value of the ruName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setRuName(String value) {
    this.ruName = value;
  }

  /**
   * Gets the value of the tradingStyle property.
   *
   * @return possible object is {@link String }
   */
  public String getTradingStyle() {
    return tradingStyle;
  }

  /**
   * Sets the value of the tradingStyle property.
   *
   * @param value allowed object is {@link String }
   */
  public void setTradingStyle(String value) {
    this.tradingStyle = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionContact.Builder<_B> _other) {
    _other.title = this.title;
    _other.forename = this.forename;
    _other.surname = this.surname;
    _other.phoneNumber = this.phoneNumber;
    _other.emailAddress = this.emailAddress;
    _other.ruName = this.ruName;
    _other.tradingStyle = this.tradingStyle;
  }

  public <_B> ActionContact.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionContact.Builder<_B>(_parentBuilder, this, true);
  }

  public ActionContact.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionContact.Builder<Void> builder() {
    return new ActionContact.Builder<Void>(null, null, false);
  }

  public static <_B> ActionContact.Builder<_B> copyOf(final ActionContact _other) {
    final ActionContact.Builder<_B> _newBuilder = new ActionContact.Builder<_B>(null, null, false);
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
      final ActionContact.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree titlePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("title"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (titlePropertyTree != null)
        : ((titlePropertyTree == null) || (!titlePropertyTree.isLeaf())))) {
      _other.title = this.title;
    }
    final PropertyTree forenamePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("forename"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (forenamePropertyTree != null)
        : ((forenamePropertyTree == null) || (!forenamePropertyTree.isLeaf())))) {
      _other.forename = this.forename;
    }
    final PropertyTree surnamePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("surname"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (surnamePropertyTree != null)
        : ((surnamePropertyTree == null) || (!surnamePropertyTree.isLeaf())))) {
      _other.surname = this.surname;
    }
    final PropertyTree phoneNumberPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("phoneNumber"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (phoneNumberPropertyTree != null)
        : ((phoneNumberPropertyTree == null) || (!phoneNumberPropertyTree.isLeaf())))) {
      _other.phoneNumber = this.phoneNumber;
    }
    final PropertyTree emailAddressPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("emailAddress"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (emailAddressPropertyTree != null)
        : ((emailAddressPropertyTree == null) || (!emailAddressPropertyTree.isLeaf())))) {
      _other.emailAddress = this.emailAddress;
    }
    final PropertyTree ruNamePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("ruName"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (ruNamePropertyTree != null)
        : ((ruNamePropertyTree == null) || (!ruNamePropertyTree.isLeaf())))) {
      _other.ruName = this.ruName;
    }
    final PropertyTree tradingStylePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("tradingStyle"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (tradingStylePropertyTree != null)
        : ((tradingStylePropertyTree == null) || (!tradingStylePropertyTree.isLeaf())))) {
      _other.tradingStyle = this.tradingStyle;
    }
  }

  public <_B> ActionContact.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionContact.Builder<_B>(
        _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public ActionContact.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionContact.Builder<_B> copyOf(
      final ActionContact _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionContact.Builder<_B> _newBuilder = new ActionContact.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionContact.Builder<Void> copyExcept(
      final ActionContact _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionContact.Builder<Void> copyOnly(
      final ActionContact _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public boolean equals(Object object) {
    if ((object == null) || (this.getClass() != object.getClass())) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final ActionContact that = ((ActionContact) object);
    {
      String leftTitle;
      leftTitle = this.getTitle();
      String rightTitle;
      rightTitle = that.getTitle();
      if (this.title != null) {
        if (that.title != null) {
          if (!leftTitle.equals(rightTitle)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.title != null) {
          return false;
        }
      }
    }
    {
      String leftForename;
      leftForename = this.getForename();
      String rightForename;
      rightForename = that.getForename();
      if (this.forename != null) {
        if (that.forename != null) {
          if (!leftForename.equals(rightForename)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.forename != null) {
          return false;
        }
      }
    }
    {
      String leftSurname;
      leftSurname = this.getSurname();
      String rightSurname;
      rightSurname = that.getSurname();
      if (this.surname != null) {
        if (that.surname != null) {
          if (!leftSurname.equals(rightSurname)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.surname != null) {
          return false;
        }
      }
    }
    {
      String leftPhoneNumber;
      leftPhoneNumber = this.getPhoneNumber();
      String rightPhoneNumber;
      rightPhoneNumber = that.getPhoneNumber();
      if (this.phoneNumber != null) {
        if (that.phoneNumber != null) {
          if (!leftPhoneNumber.equals(rightPhoneNumber)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.phoneNumber != null) {
          return false;
        }
      }
    }
    {
      String leftEmailAddress;
      leftEmailAddress = this.getEmailAddress();
      String rightEmailAddress;
      rightEmailAddress = that.getEmailAddress();
      if (this.emailAddress != null) {
        if (that.emailAddress != null) {
          if (!leftEmailAddress.equals(rightEmailAddress)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.emailAddress != null) {
          return false;
        }
      }
    }
    {
      String leftRuName;
      leftRuName = this.getRuName();
      String rightRuName;
      rightRuName = that.getRuName();
      if (this.ruName != null) {
        if (that.ruName != null) {
          if (!leftRuName.equals(rightRuName)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.ruName != null) {
          return false;
        }
      }
    }
    {
      String leftTradingStyle;
      leftTradingStyle = this.getTradingStyle();
      String rightTradingStyle;
      rightTradingStyle = that.getTradingStyle();
      if (this.tradingStyle != null) {
        if (that.tradingStyle != null) {
          if (!leftTradingStyle.equals(rightTradingStyle)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.tradingStyle != null) {
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
      String theTitle;
      theTitle = this.getTitle();
      if (this.title != null) {
        currentHashCode += theTitle.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theForename;
      theForename = this.getForename();
      if (this.forename != null) {
        currentHashCode += theForename.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theSurname;
      theSurname = this.getSurname();
      if (this.surname != null) {
        currentHashCode += theSurname.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String thePhoneNumber;
      thePhoneNumber = this.getPhoneNumber();
      if (this.phoneNumber != null) {
        currentHashCode += thePhoneNumber.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theEmailAddress;
      theEmailAddress = this.getEmailAddress();
      if (this.emailAddress != null) {
        currentHashCode += theEmailAddress.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theRuName;
      theRuName = this.getRuName();
      if (this.ruName != null) {
        currentHashCode += theRuName.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theTradingStyle;
      theTradingStyle = this.getTradingStyle();
      if (this.tradingStyle != null) {
        currentHashCode += theTradingStyle.hashCode();
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
      String theTitle;
      theTitle = this.getTitle();
      strategy.appendField(locator, this, "title", buffer, theTitle, (this.title != null));
    }
    {
      String theForename;
      theForename = this.getForename();
      strategy.appendField(locator, this, "forename", buffer, theForename, (this.forename != null));
    }
    {
      String theSurname;
      theSurname = this.getSurname();
      strategy.appendField(locator, this, "surname", buffer, theSurname, (this.surname != null));
    }
    {
      String thePhoneNumber;
      thePhoneNumber = this.getPhoneNumber();
      strategy.appendField(
          locator, this, "phoneNumber", buffer, thePhoneNumber, (this.phoneNumber != null));
    }
    {
      String theEmailAddress;
      theEmailAddress = this.getEmailAddress();
      strategy.appendField(
          locator, this, "emailAddress", buffer, theEmailAddress, (this.emailAddress != null));
    }
    {
      String theRuName;
      theRuName = this.getRuName();
      strategy.appendField(locator, this, "ruName", buffer, theRuName, (this.ruName != null));
    }
    {
      String theTradingStyle;
      theTradingStyle = this.getTradingStyle();
      strategy.appendField(
          locator, this, "tradingStyle", buffer, theTradingStyle, (this.tradingStyle != null));
    }
    return buffer;
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final ActionContact _storedValue;
    private String title;
    private String forename;
    private String surname;
    private String phoneNumber;
    private String emailAddress;
    private String ruName;
    private String tradingStyle;

    public Builder(final _B _parentBuilder, final ActionContact _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.title = _other.title;
          this.forename = _other.forename;
          this.surname = _other.surname;
          this.phoneNumber = _other.phoneNumber;
          this.emailAddress = _other.emailAddress;
          this.ruName = _other.ruName;
          this.tradingStyle = _other.tradingStyle;
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionContact _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree titlePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("title"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (titlePropertyTree != null)
              : ((titlePropertyTree == null) || (!titlePropertyTree.isLeaf())))) {
            this.title = _other.title;
          }
          final PropertyTree forenamePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("forename"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (forenamePropertyTree != null)
              : ((forenamePropertyTree == null) || (!forenamePropertyTree.isLeaf())))) {
            this.forename = _other.forename;
          }
          final PropertyTree surnamePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("surname"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (surnamePropertyTree != null)
              : ((surnamePropertyTree == null) || (!surnamePropertyTree.isLeaf())))) {
            this.surname = _other.surname;
          }
          final PropertyTree phoneNumberPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("phoneNumber"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (phoneNumberPropertyTree != null)
              : ((phoneNumberPropertyTree == null) || (!phoneNumberPropertyTree.isLeaf())))) {
            this.phoneNumber = _other.phoneNumber;
          }
          final PropertyTree emailAddressPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("emailAddress"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (emailAddressPropertyTree != null)
              : ((emailAddressPropertyTree == null) || (!emailAddressPropertyTree.isLeaf())))) {
            this.emailAddress = _other.emailAddress;
          }
          final PropertyTree ruNamePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("ruName"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (ruNamePropertyTree != null)
              : ((ruNamePropertyTree == null) || (!ruNamePropertyTree.isLeaf())))) {
            this.ruName = _other.ruName;
          }
          final PropertyTree tradingStylePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("tradingStyle"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (tradingStylePropertyTree != null)
              : ((tradingStylePropertyTree == null) || (!tradingStylePropertyTree.isLeaf())))) {
            this.tradingStyle = _other.tradingStyle;
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

    protected <_P extends ActionContact> _P init(final _P _product) {
      _product.title = this.title;
      _product.forename = this.forename;
      _product.surname = this.surname;
      _product.phoneNumber = this.phoneNumber;
      _product.emailAddress = this.emailAddress;
      _product.ruName = this.ruName;
      _product.tradingStyle = this.tradingStyle;
      return _product;
    }

    /**
     * Sets the new value of "title" (any previous value will be replaced)
     *
     * @param title New value of the "title" property.
     */
    public ActionContact.Builder<_B> withTitle(final String title) {
      this.title = title;
      return this;
    }

    /**
     * Sets the new value of "forename" (any previous value will be replaced)
     *
     * @param forename New value of the "forename" property.
     */
    public ActionContact.Builder<_B> withForename(final String forename) {
      this.forename = forename;
      return this;
    }

    /**
     * Sets the new value of "surname" (any previous value will be replaced)
     *
     * @param surname New value of the "surname" property.
     */
    public ActionContact.Builder<_B> withSurname(final String surname) {
      this.surname = surname;
      return this;
    }

    /**
     * Sets the new value of "phoneNumber" (any previous value will be replaced)
     *
     * @param phoneNumber New value of the "phoneNumber" property.
     */
    public ActionContact.Builder<_B> withPhoneNumber(final String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    /**
     * Sets the new value of "emailAddress" (any previous value will be replaced)
     *
     * @param emailAddress New value of the "emailAddress" property.
     */
    public ActionContact.Builder<_B> withEmailAddress(final String emailAddress) {
      this.emailAddress = emailAddress;
      return this;
    }

    /**
     * Sets the new value of "ruName" (any previous value will be replaced)
     *
     * @param ruName New value of the "ruName" property.
     */
    public ActionContact.Builder<_B> withRuName(final String ruName) {
      this.ruName = ruName;
      return this;
    }

    /**
     * Sets the new value of "tradingStyle" (any previous value will be replaced)
     *
     * @param tradingStyle New value of the "tradingStyle" property.
     */
    public ActionContact.Builder<_B> withTradingStyle(final String tradingStyle) {
      this.tradingStyle = tradingStyle;
      return this;
    }

    @Override
    public ActionContact build() {
      if (_storedValue == null) {
        return this.init(new ActionContact());
      } else {
        return ((ActionContact) _storedValue);
      }
    }
  }

  public static class Select extends ActionContact.Selector<ActionContact.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionContact.Select _root() {
      return new ActionContact.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> title = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> forename =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> surname =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> phoneNumber =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>
        emailAddress = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> ruName =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>
        tradingStyle = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.title != null) {
        products.put("title", this.title.init());
      }
      if (this.forename != null) {
        products.put("forename", this.forename.init());
      }
      if (this.surname != null) {
        products.put("surname", this.surname.init());
      }
      if (this.phoneNumber != null) {
        products.put("phoneNumber", this.phoneNumber.init());
      }
      if (this.emailAddress != null) {
        products.put("emailAddress", this.emailAddress.init());
      }
      if (this.ruName != null) {
        products.put("ruName", this.ruName.init());
      }
      if (this.tradingStyle != null) {
        products.put("tradingStyle", this.tradingStyle.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> title() {
      return ((this.title == null)
          ? this.title =
              new com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>(
                  this._root, this, "title")
          : this.title);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> forename() {
      return ((this.forename == null)
          ? this.forename =
              new com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>(
                  this._root, this, "forename")
          : this.forename);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> surname() {
      return ((this.surname == null)
          ? this.surname =
              new com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>(
                  this._root, this, "surname")
          : this.surname);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>
        phoneNumber() {
      return ((this.phoneNumber == null)
          ? this.phoneNumber =
              new com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>(
                  this._root, this, "phoneNumber")
          : this.phoneNumber);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>
        emailAddress() {
      return ((this.emailAddress == null)
          ? this.emailAddress =
              new com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>(
                  this._root, this, "emailAddress")
          : this.emailAddress);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>> ruName() {
      return ((this.ruName == null)
          ? this.ruName =
              new com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>(
                  this._root, this, "ruName")
          : this.ruName);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>
        tradingStyle() {
      return ((this.tradingStyle == null)
          ? this.tradingStyle =
              new com.kscs.util.jaxb.Selector<TRoot, ActionContact.Selector<TRoot, TParent>>(
                  this._root, this, "tradingStyle")
          : this.tradingStyle);
    }
  }
}
