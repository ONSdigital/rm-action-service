package uk.gov.ons.ctp.response.action.message.instruction;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * Java class for ActionCancel complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionCancel"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ons.gov.uk/ctp/response/action/message/instruction}Action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="reason" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="caseId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="caseRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionCancel",
    propOrder = {"reason", "caseId", "caseRef"})
public class ActionCancel extends Action implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;

  @XmlElement(required = true)
  protected String reason;

  protected String caseId;
  protected String caseRef;

  /** Default no-arg constructor */
  public ActionCancel() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionCancel(
      final String actionId,
      final boolean responseRequired,
      final String reason,
      final String caseId,
      final String caseRef) {
    super(actionId, responseRequired);
    this.reason = reason;
    this.caseId = caseId;
    this.caseRef = caseRef;
  }

  /**
   * Gets the value of the reason property.
   *
   * @return possible object is {@link String }
   */
  public String getReason() {
    return reason;
  }

  /**
   * Sets the value of the reason property.
   *
   * @param value allowed object is {@link String }
   */
  public void setReason(String value) {
    this.reason = value;
  }

  /**
   * Gets the value of the caseId property.
   *
   * @return possible object is {@link String }
   */
  public String getCaseId() {
    return caseId;
  }

  /**
   * Sets the value of the caseId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCaseId(String value) {
    this.caseId = value;
  }

  /**
   * Gets the value of the caseRef property.
   *
   * @return possible object is {@link String }
   */
  public String getCaseRef() {
    return caseRef;
  }

  /**
   * Sets the value of the caseRef property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCaseRef(String value) {
    this.caseRef = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionCancel.Builder<_B> _other) {
    super.copyTo(_other);
    _other.reason = this.reason;
    _other.caseId = this.caseId;
    _other.caseRef = this.caseRef;
  }

  @Override
  public <_B> ActionCancel.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionCancel.Builder<_B>(_parentBuilder, this, true);
  }

  @Override
  public ActionCancel.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionCancel.Builder<Void> builder() {
    return new ActionCancel.Builder<Void>(null, null, false);
  }

  public static <_B> ActionCancel.Builder<_B> copyOf(final Action _other) {
    final ActionCancel.Builder<_B> _newBuilder = new ActionCancel.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder);
    return _newBuilder;
  }

  public static <_B> ActionCancel.Builder<_B> copyOf(final ActionCancel _other) {
    final ActionCancel.Builder<_B> _newBuilder = new ActionCancel.Builder<_B>(null, null, false);
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
      final ActionCancel.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    super.copyTo(_other, _propertyTree, _propertyTreeUse);
    final PropertyTree reasonPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("reason"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (reasonPropertyTree != null)
        : ((reasonPropertyTree == null) || (!reasonPropertyTree.isLeaf())))) {
      _other.reason = this.reason;
    }
    final PropertyTree caseIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (caseIdPropertyTree != null)
        : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
      _other.caseId = this.caseId;
    }
    final PropertyTree caseRefPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("caseRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (caseRefPropertyTree != null)
        : ((caseRefPropertyTree == null) || (!caseRefPropertyTree.isLeaf())))) {
      _other.caseRef = this.caseRef;
    }
  }

  @Override
  public <_B> ActionCancel.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionCancel.Builder<_B>(
        _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  @Override
  public ActionCancel.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionCancel.Builder<_B> copyOf(
      final Action _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionCancel.Builder<_B> _newBuilder = new ActionCancel.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static <_B> ActionCancel.Builder<_B> copyOf(
      final ActionCancel _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionCancel.Builder<_B> _newBuilder = new ActionCancel.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionCancel.Builder<Void> copyExcept(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionCancel.Builder<Void> copyExcept(
      final ActionCancel _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionCancel.Builder<Void> copyOnly(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public static ActionCancel.Builder<Void> copyOnly(
      final ActionCancel _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public boolean equals(Object object) {
    if ((object == null) || (this.getClass() != object.getClass())) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (!super.equals(object)) {
      return false;
    }
    final ActionCancel that = ((ActionCancel) object);
    {
      String leftReason;
      leftReason = this.getReason();
      String rightReason;
      rightReason = that.getReason();
      if (this.reason != null) {
        if (that.reason != null) {
          if (!leftReason.equals(rightReason)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.reason != null) {
          return false;
        }
      }
    }
    {
      String leftCaseId;
      leftCaseId = this.getCaseId();
      String rightCaseId;
      rightCaseId = that.getCaseId();
      if (this.caseId != null) {
        if (that.caseId != null) {
          if (!leftCaseId.equals(rightCaseId)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.caseId != null) {
          return false;
        }
      }
    }
    {
      String leftCaseRef;
      leftCaseRef = this.getCaseRef();
      String rightCaseRef;
      rightCaseRef = that.getCaseRef();
      if (this.caseRef != null) {
        if (that.caseRef != null) {
          if (!leftCaseRef.equals(rightCaseRef)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.caseRef != null) {
          return false;
        }
      }
    }
    return true;
  }

  public int hashCode() {
    int currentHashCode = 1;
    currentHashCode = ((currentHashCode * 31) + super.hashCode());
    {
      currentHashCode = (currentHashCode * 31);
      String theReason;
      theReason = this.getReason();
      if (this.reason != null) {
        currentHashCode += theReason.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theCaseId;
      theCaseId = this.getCaseId();
      if (this.caseId != null) {
        currentHashCode += theCaseId.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theCaseRef;
      theCaseRef = this.getCaseRef();
      if (this.caseRef != null) {
        currentHashCode += theCaseRef.hashCode();
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
    super.appendFields(locator, buffer, strategy);
    {
      String theReason;
      theReason = this.getReason();
      strategy.appendField(locator, this, "reason", buffer, theReason, (this.reason != null));
    }
    {
      String theCaseId;
      theCaseId = this.getCaseId();
      strategy.appendField(locator, this, "caseId", buffer, theCaseId, (this.caseId != null));
    }
    {
      String theCaseRef;
      theCaseRef = this.getCaseRef();
      strategy.appendField(locator, this, "caseRef", buffer, theCaseRef, (this.caseRef != null));
    }
    return buffer;
  }

  public static class Builder<_B> extends Action.Builder<_B> implements Buildable {

    private String reason;
    private String caseId;
    private String caseRef;

    public Builder(final _B _parentBuilder, final ActionCancel _other, final boolean _copy) {
      super(_parentBuilder, _other, _copy);
      if (_other != null) {
        this.reason = _other.reason;
        this.caseId = _other.caseId;
        this.caseRef = _other.caseRef;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionCancel _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      super(_parentBuilder, _other, _copy, _propertyTree, _propertyTreeUse);
      if (_other != null) {
        final PropertyTree reasonPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("reason"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (reasonPropertyTree != null)
            : ((reasonPropertyTree == null) || (!reasonPropertyTree.isLeaf())))) {
          this.reason = _other.reason;
        }
        final PropertyTree caseIdPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (caseIdPropertyTree != null)
            : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
          this.caseId = _other.caseId;
        }
        final PropertyTree caseRefPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("caseRef"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (caseRefPropertyTree != null)
            : ((caseRefPropertyTree == null) || (!caseRefPropertyTree.isLeaf())))) {
          this.caseRef = _other.caseRef;
        }
      }
    }

    protected <_P extends ActionCancel> _P init(final _P _product) {
      _product.reason = this.reason;
      _product.caseId = this.caseId;
      _product.caseRef = this.caseRef;
      return super.init(_product);
    }

    /**
     * Sets the new value of "reason" (any previous value will be replaced)
     *
     * @param reason New value of the "reason" property.
     */
    public ActionCancel.Builder<_B> withReason(final String reason) {
      this.reason = reason;
      return this;
    }

    /**
     * Sets the new value of "caseId" (any previous value will be replaced)
     *
     * @param caseId New value of the "caseId" property.
     */
    public ActionCancel.Builder<_B> withCaseId(final String caseId) {
      this.caseId = caseId;
      return this;
    }

    /**
     * Sets the new value of "caseRef" (any previous value will be replaced)
     *
     * @param caseRef New value of the "caseRef" property.
     */
    public ActionCancel.Builder<_B> withCaseRef(final String caseRef) {
      this.caseRef = caseRef;
      return this;
    }

    /**
     * Sets the new value of "actionId" (any previous value will be replaced)
     *
     * @param actionId New value of the "actionId" property.
     */
    @Override
    public ActionCancel.Builder<_B> withActionId(final String actionId) {
      super.withActionId(actionId);
      return this;
    }

    /**
     * Sets the new value of "responseRequired" (any previous value will be replaced)
     *
     * @param responseRequired New value of the "responseRequired" property.
     */
    @Override
    public ActionCancel.Builder<_B> withResponseRequired(final boolean responseRequired) {
      super.withResponseRequired(responseRequired);
      return this;
    }

    @Override
    public ActionCancel build() {
      if (_storedValue == null) {
        return this.init(new ActionCancel());
      } else {
        return ((ActionCancel) _storedValue);
      }
    }
  }

  public static class Select extends ActionCancel.Selector<ActionCancel.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionCancel.Select _root() {
      return new ActionCancel.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends Action.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>> reason = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>> caseId = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>> caseRef =
        null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.reason != null) {
        products.put("reason", this.reason.init());
      }
      if (this.caseId != null) {
        products.put("caseId", this.caseId.init());
      }
      if (this.caseRef != null) {
        products.put("caseRef", this.caseRef.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>> reason() {
      return ((this.reason == null)
          ? this.reason =
              new com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>>(
                  this._root, this, "reason")
          : this.reason);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>> caseId() {
      return ((this.caseId == null)
          ? this.caseId =
              new com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>>(
                  this._root, this, "caseId")
          : this.caseId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>> caseRef() {
      return ((this.caseRef == null)
          ? this.caseRef =
              new com.kscs.util.jaxb.Selector<TRoot, ActionCancel.Selector<TRoot, TParent>>(
                  this._root, this, "caseRef")
          : this.caseRef);
    }
  }
}
