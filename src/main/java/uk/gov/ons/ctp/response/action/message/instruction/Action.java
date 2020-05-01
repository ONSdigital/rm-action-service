package uk.gov.ons.ctp.response.action.message.instruction;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * Java class for Action complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Action"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="actionId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="responseRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Action",
    propOrder = {"actionId", "responseRequired"})
@XmlSeeAlso({ActionCancel.class, ActionUpdate.class, ActionRequest.class})
public class Action implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;

  @XmlElement(required = true)
  protected String actionId;

  protected boolean responseRequired;

  /** Default no-arg constructor */
  public Action() {
    super();
  }

  /** Fully-initialising value constructor */
  public Action(final String actionId, final boolean responseRequired) {
    this.actionId = actionId;
    this.responseRequired = responseRequired;
  }

  /**
   * Gets the value of the actionId property.
   *
   * @return possible object is {@link String }
   */
  public String getActionId() {
    return actionId;
  }

  /**
   * Sets the value of the actionId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setActionId(String value) {
    this.actionId = value;
  }

  /** Gets the value of the responseRequired property. */
  public boolean isResponseRequired() {
    return responseRequired;
  }

  /** Sets the value of the responseRequired property. */
  public void setResponseRequired(boolean value) {
    this.responseRequired = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final Action.Builder<_B> _other) {
    _other.actionId = this.actionId;
    _other.responseRequired = this.responseRequired;
  }

  public <_B> Action.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new Action.Builder<_B>(_parentBuilder, this, true);
  }

  public Action.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static Action.Builder<Void> builder() {
    return new Action.Builder<Void>(null, null, false);
  }

  public static <_B> Action.Builder<_B> copyOf(final Action _other) {
    final Action.Builder<_B> _newBuilder = new Action.Builder<_B>(null, null, false);
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
      final Action.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree actionIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionIdPropertyTree != null)
        : ((actionIdPropertyTree == null) || (!actionIdPropertyTree.isLeaf())))) {
      _other.actionId = this.actionId;
    }
    final PropertyTree responseRequiredPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("responseRequired"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (responseRequiredPropertyTree != null)
        : ((responseRequiredPropertyTree == null) || (!responseRequiredPropertyTree.isLeaf())))) {
      _other.responseRequired = this.responseRequired;
    }
  }

  public <_B> Action.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new Action.Builder<_B>(_parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public Action.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> Action.Builder<_B> copyOf(
      final Action _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final Action.Builder<_B> _newBuilder = new Action.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static Action.Builder<Void> copyExcept(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static Action.Builder<Void> copyOnly(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public boolean equals(Object object) {
    if ((object == null) || (this.getClass() != object.getClass())) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final Action that = ((Action) object);
    {
      String leftActionId;
      leftActionId = this.getActionId();
      String rightActionId;
      rightActionId = that.getActionId();
      if (this.actionId != null) {
        if (that.actionId != null) {
          if (!leftActionId.equals(rightActionId)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.actionId != null) {
          return false;
        }
      }
    }
    {
      boolean leftResponseRequired;
      leftResponseRequired = this.isResponseRequired();
      boolean rightResponseRequired;
      rightResponseRequired = that.isResponseRequired();
      if (leftResponseRequired != rightResponseRequired) {
        return false;
      }
    }
    return true;
  }

  public int hashCode() {
    int currentHashCode = 1;
    {
      currentHashCode = (currentHashCode * 31);
      String theActionId;
      theActionId = this.getActionId();
      if (this.actionId != null) {
        currentHashCode += theActionId.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      boolean theResponseRequired;
      theResponseRequired = this.isResponseRequired();
      currentHashCode += (theResponseRequired ? 1231 : 1237);
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
      String theActionId;
      theActionId = this.getActionId();
      strategy.appendField(locator, this, "actionId", buffer, theActionId, (this.actionId != null));
    }
    {
      boolean theResponseRequired;
      theResponseRequired = this.isResponseRequired();
      strategy.appendField(locator, this, "responseRequired", buffer, theResponseRequired, true);
    }
    return buffer;
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final Action _storedValue;
    private String actionId;
    private boolean responseRequired;

    public Builder(final _B _parentBuilder, final Action _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.actionId = _other.actionId;
          this.responseRequired = _other.responseRequired;
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final Action _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree actionIdPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("actionId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (actionIdPropertyTree != null)
              : ((actionIdPropertyTree == null) || (!actionIdPropertyTree.isLeaf())))) {
            this.actionId = _other.actionId;
          }
          final PropertyTree responseRequiredPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("responseRequired"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (responseRequiredPropertyTree != null)
              : ((responseRequiredPropertyTree == null)
                  || (!responseRequiredPropertyTree.isLeaf())))) {
            this.responseRequired = _other.responseRequired;
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

    protected <_P extends Action> _P init(final _P _product) {
      _product.actionId = this.actionId;
      _product.responseRequired = this.responseRequired;
      return _product;
    }

    /**
     * Sets the new value of "actionId" (any previous value will be replaced)
     *
     * @param actionId New value of the "actionId" property.
     */
    public Action.Builder<_B> withActionId(final String actionId) {
      this.actionId = actionId;
      return this;
    }

    /**
     * Sets the new value of "responseRequired" (any previous value will be replaced)
     *
     * @param responseRequired New value of the "responseRequired" property.
     */
    public Action.Builder<_B> withResponseRequired(final boolean responseRequired) {
      this.responseRequired = responseRequired;
      return this;
    }

    @Override
    public Action build() {
      if (_storedValue == null) {
        return this.init(new Action());
      } else {
        return ((Action) _storedValue);
      }
    }
  }

  public static class Select extends Action.Selector<Action.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static Action.Select _root() {
      return new Action.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, Action.Selector<TRoot, TParent>> actionId = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.actionId != null) {
        products.put("actionId", this.actionId.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, Action.Selector<TRoot, TParent>> actionId() {
      return ((this.actionId == null)
          ? this.actionId =
              new com.kscs.util.jaxb.Selector<TRoot, Action.Selector<TRoot, TParent>>(
                  this._root, this, "actionId")
          : this.actionId);
    }
  }
}
