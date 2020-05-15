package uk.gov.ons.ctp.response.action.message.instruction;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * Java class for ActionInstruction complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionInstruction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="actionRequest" type="{http://ons.gov.uk/ctp/response/action/message/instruction}ActionRequest"/&gt;
 *         &lt;element name="actionUpdate" type="{http://ons.gov.uk/ctp/response/action/message/instruction}ActionUpdate"/&gt;
 *         &lt;element name="actionCancel" type="{http://ons.gov.uk/ctp/response/action/message/instruction}ActionCancel"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionInstruction",
    propOrder = {"actionCancel", "actionUpdate", "actionRequest"})
@XmlRootElement(name = "actionInstruction")
public class ActionInstruction implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;
  protected ActionCancel actionCancel;
  protected ActionUpdate actionUpdate;
  protected ActionRequest actionRequest;

  /** Default no-arg constructor */
  public ActionInstruction() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionInstruction(
      final ActionCancel actionCancel,
      final ActionUpdate actionUpdate,
      final ActionRequest actionRequest) {
    this.actionCancel = actionCancel;
    this.actionUpdate = actionUpdate;
    this.actionRequest = actionRequest;
  }

  /**
   * Gets the value of the actionCancel property.
   *
   * @return possible object is {@link ActionCancel }
   */
  public ActionCancel getActionCancel() {
    return actionCancel;
  }

  /**
   * Sets the value of the actionCancel property.
   *
   * @param value allowed object is {@link ActionCancel }
   */
  public void setActionCancel(ActionCancel value) {
    this.actionCancel = value;
  }

  /**
   * Gets the value of the actionUpdate property.
   *
   * @return possible object is {@link ActionUpdate }
   */
  public ActionUpdate getActionUpdate() {
    return actionUpdate;
  }

  /**
   * Sets the value of the actionUpdate property.
   *
   * @param value allowed object is {@link ActionUpdate }
   */
  public void setActionUpdate(ActionUpdate value) {
    this.actionUpdate = value;
  }

  /**
   * Gets the value of the actionRequest property.
   *
   * @return possible object is {@link ActionRequest }
   */
  public ActionRequest getActionRequest() {
    return actionRequest;
  }

  /**
   * Sets the value of the actionRequest property.
   *
   * @param value allowed object is {@link ActionRequest }
   */
  public void setActionRequest(ActionRequest value) {
    this.actionRequest = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionInstruction.Builder<_B> _other) {
    _other.actionCancel =
        ((this.actionCancel == null) ? null : this.actionCancel.newCopyBuilder(_other));
    _other.actionUpdate =
        ((this.actionUpdate == null) ? null : this.actionUpdate.newCopyBuilder(_other));
    _other.actionRequest =
        ((this.actionRequest == null) ? null : this.actionRequest.newCopyBuilder(_other));
  }

  public <_B> ActionInstruction.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionInstruction.Builder<_B>(_parentBuilder, this, true);
  }

  public ActionInstruction.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionInstruction.Builder<Void> builder() {
    return new ActionInstruction.Builder<Void>(null, null, false);
  }

  public static <_B> ActionInstruction.Builder<_B> copyOf(final ActionInstruction _other) {
    final ActionInstruction.Builder<_B> _newBuilder =
        new ActionInstruction.Builder<_B>(null, null, false);
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
      final ActionInstruction.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree actionCancelPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionCancel"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionCancelPropertyTree != null)
        : ((actionCancelPropertyTree == null) || (!actionCancelPropertyTree.isLeaf())))) {
      _other.actionCancel =
          ((this.actionCancel == null)
              ? null
              : this.actionCancel.newCopyBuilder(
                  _other, actionCancelPropertyTree, _propertyTreeUse));
    }
    final PropertyTree actionUpdatePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionUpdate"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionUpdatePropertyTree != null)
        : ((actionUpdatePropertyTree == null) || (!actionUpdatePropertyTree.isLeaf())))) {
      _other.actionUpdate =
          ((this.actionUpdate == null)
              ? null
              : this.actionUpdate.newCopyBuilder(
                  _other, actionUpdatePropertyTree, _propertyTreeUse));
    }
    final PropertyTree actionRequestPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionRequest"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionRequestPropertyTree != null)
        : ((actionRequestPropertyTree == null) || (!actionRequestPropertyTree.isLeaf())))) {
      _other.actionRequest =
          ((this.actionRequest == null)
              ? null
              : this.actionRequest.newCopyBuilder(
                  _other, actionRequestPropertyTree, _propertyTreeUse));
    }
  }

  public <_B> ActionInstruction.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionInstruction.Builder<_B>(
        _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public ActionInstruction.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionInstruction.Builder<_B> copyOf(
      final ActionInstruction _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionInstruction.Builder<_B> _newBuilder =
        new ActionInstruction.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionInstruction.Builder<Void> copyExcept(
      final ActionInstruction _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionInstruction.Builder<Void> copyOnly(
      final ActionInstruction _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public boolean equals(Object object) {
    if ((object == null) || (this.getClass() != object.getClass())) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final ActionInstruction that = ((ActionInstruction) object);
    {
      ActionCancel leftActionCancel;
      leftActionCancel = this.getActionCancel();
      ActionCancel rightActionCancel;
      rightActionCancel = that.getActionCancel();
      if (this.actionCancel != null) {
        if (that.actionCancel != null) {
          if (!leftActionCancel.equals(rightActionCancel)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.actionCancel != null) {
          return false;
        }
      }
    }
    {
      ActionUpdate leftActionUpdate;
      leftActionUpdate = this.getActionUpdate();
      ActionUpdate rightActionUpdate;
      rightActionUpdate = that.getActionUpdate();
      if (this.actionUpdate != null) {
        if (that.actionUpdate != null) {
          if (!leftActionUpdate.equals(rightActionUpdate)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.actionUpdate != null) {
          return false;
        }
      }
    }
    {
      ActionRequest leftActionRequest;
      leftActionRequest = this.getActionRequest();
      ActionRequest rightActionRequest;
      rightActionRequest = that.getActionRequest();
      if (this.actionRequest != null) {
        if (that.actionRequest != null) {
          if (!leftActionRequest.equals(rightActionRequest)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.actionRequest != null) {
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
      ActionCancel theActionCancel;
      theActionCancel = this.getActionCancel();
      if (this.actionCancel != null) {
        currentHashCode += theActionCancel.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      ActionUpdate theActionUpdate;
      theActionUpdate = this.getActionUpdate();
      if (this.actionUpdate != null) {
        currentHashCode += theActionUpdate.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      ActionRequest theActionRequest;
      theActionRequest = this.getActionRequest();
      if (this.actionRequest != null) {
        currentHashCode += theActionRequest.hashCode();
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
      ActionCancel theActionCancel;
      theActionCancel = this.getActionCancel();
      strategy.appendField(
          locator, this, "actionCancel", buffer, theActionCancel, (this.actionCancel != null));
    }
    {
      ActionUpdate theActionUpdate;
      theActionUpdate = this.getActionUpdate();
      strategy.appendField(
          locator, this, "actionUpdate", buffer, theActionUpdate, (this.actionUpdate != null));
    }
    {
      ActionRequest theActionRequest;
      theActionRequest = this.getActionRequest();
      strategy.appendField(
          locator, this, "actionRequest", buffer, theActionRequest, (this.actionRequest != null));
    }
    return buffer;
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final ActionInstruction _storedValue;
    private ActionCancel.Builder<ActionInstruction.Builder<_B>> actionCancel;
    private ActionUpdate.Builder<ActionInstruction.Builder<_B>> actionUpdate;
    private ActionRequest.Builder<ActionInstruction.Builder<_B>> actionRequest;

    public Builder(final _B _parentBuilder, final ActionInstruction _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.actionCancel =
              ((_other.actionCancel == null) ? null : _other.actionCancel.newCopyBuilder(this));
          this.actionUpdate =
              ((_other.actionUpdate == null) ? null : _other.actionUpdate.newCopyBuilder(this));
          this.actionRequest =
              ((_other.actionRequest == null) ? null : _other.actionRequest.newCopyBuilder(this));
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionInstruction _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree actionCancelPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("actionCancel"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (actionCancelPropertyTree != null)
              : ((actionCancelPropertyTree == null) || (!actionCancelPropertyTree.isLeaf())))) {
            this.actionCancel =
                ((_other.actionCancel == null)
                    ? null
                    : _other.actionCancel.newCopyBuilder(
                        this, actionCancelPropertyTree, _propertyTreeUse));
          }
          final PropertyTree actionUpdatePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("actionUpdate"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (actionUpdatePropertyTree != null)
              : ((actionUpdatePropertyTree == null) || (!actionUpdatePropertyTree.isLeaf())))) {
            this.actionUpdate =
                ((_other.actionUpdate == null)
                    ? null
                    : _other.actionUpdate.newCopyBuilder(
                        this, actionUpdatePropertyTree, _propertyTreeUse));
          }
          final PropertyTree actionRequestPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("actionRequest"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (actionRequestPropertyTree != null)
              : ((actionRequestPropertyTree == null) || (!actionRequestPropertyTree.isLeaf())))) {
            this.actionRequest =
                ((_other.actionRequest == null)
                    ? null
                    : _other.actionRequest.newCopyBuilder(
                        this, actionRequestPropertyTree, _propertyTreeUse));
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

    protected <_P extends ActionInstruction> _P init(final _P _product) {
      _product.actionCancel = ((this.actionCancel == null) ? null : this.actionCancel.build());
      _product.actionUpdate = ((this.actionUpdate == null) ? null : this.actionUpdate.build());
      _product.actionRequest = ((this.actionRequest == null) ? null : this.actionRequest.build());
      return _product;
    }

    /**
     * Sets the new value of "actionCancel" (any previous value will be replaced)
     *
     * @param actionCancel New value of the "actionCancel" property.
     */
    public ActionInstruction.Builder<_B> withActionCancel(final ActionCancel actionCancel) {
      this.actionCancel =
          ((actionCancel == null)
              ? null
              : new ActionCancel.Builder<ActionInstruction.Builder<_B>>(this, actionCancel, false));
      return this;
    }

    /**
     * Returns a new builder to build the value of the "actionCancel" property (replacing previous
     * value). Use {@link
     * uk.gov.ons.ctp.response.action.message.instruction.ActionCancel.Builder#end()} to return to
     * the current builder.
     *
     * @return A new builder to build the value of the "actionCancel" property. Use {@link
     *     uk.gov.ons.ctp.response.action.message.instruction.ActionCancel.Builder#end()} to return
     *     to the current builder.
     */
    public ActionCancel.Builder<? extends ActionInstruction.Builder<_B>> withActionCancel() {
      return this.actionCancel =
          new ActionCancel.Builder<ActionInstruction.Builder<_B>>(this, null, false);
    }

    /**
     * Sets the new value of "actionUpdate" (any previous value will be replaced)
     *
     * @param actionUpdate New value of the "actionUpdate" property.
     */
    public ActionInstruction.Builder<_B> withActionUpdate(final ActionUpdate actionUpdate) {
      this.actionUpdate =
          ((actionUpdate == null)
              ? null
              : new ActionUpdate.Builder<ActionInstruction.Builder<_B>>(this, actionUpdate, false));
      return this;
    }

    /**
     * Returns a new builder to build the value of the "actionUpdate" property (replacing previous
     * value). Use {@link
     * uk.gov.ons.ctp.response.action.message.instruction.ActionUpdate.Builder#end()} to return to
     * the current builder.
     *
     * @return A new builder to build the value of the "actionUpdate" property. Use {@link
     *     uk.gov.ons.ctp.response.action.message.instruction.ActionUpdate.Builder#end()} to return
     *     to the current builder.
     */
    public ActionUpdate.Builder<? extends ActionInstruction.Builder<_B>> withActionUpdate() {
      return this.actionUpdate =
          new ActionUpdate.Builder<ActionInstruction.Builder<_B>>(this, null, false);
    }

    /**
     * Sets the new value of "actionRequest" (any previous value will be replaced)
     *
     * @param actionRequest New value of the "actionRequest" property.
     */
    public ActionInstruction.Builder<_B> withActionRequest(final ActionRequest actionRequest) {
      this.actionRequest =
          ((actionRequest == null)
              ? null
              : new ActionRequest.Builder<ActionInstruction.Builder<_B>>(
                  this, actionRequest, false));
      return this;
    }

    /**
     * Returns a new builder to build the value of the "actionRequest" property (replacing previous
     * value). Use {@link
     * uk.gov.ons.ctp.response.action.message.instruction.ActionRequest.Builder#end()} to return to
     * the current builder.
     *
     * @return A new builder to build the value of the "actionRequest" property. Use {@link
     *     uk.gov.ons.ctp.response.action.message.instruction.ActionRequest.Builder#end()} to return
     *     to the current builder.
     */
    public ActionRequest.Builder<? extends ActionInstruction.Builder<_B>> withActionRequest() {
      return this.actionRequest =
          new ActionRequest.Builder<ActionInstruction.Builder<_B>>(this, null, false);
    }

    @Override
    public ActionInstruction build() {
      if (_storedValue == null) {
        return this.init(new ActionInstruction());
      } else {
        return ((ActionInstruction) _storedValue);
      }
    }
  }

  public static class Select extends ActionInstruction.Selector<ActionInstruction.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionInstruction.Select _root() {
      return new ActionInstruction.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private ActionCancel.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>> actionCancel =
        null;
    private ActionUpdate.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>> actionUpdate =
        null;
    private ActionRequest.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>>
        actionRequest = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.actionCancel != null) {
        products.put("actionCancel", this.actionCancel.init());
      }
      if (this.actionUpdate != null) {
        products.put("actionUpdate", this.actionUpdate.init());
      }
      if (this.actionRequest != null) {
        products.put("actionRequest", this.actionRequest.init());
      }
      return products;
    }

    public ActionCancel.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>> actionCancel() {
      return ((this.actionCancel == null)
          ? this.actionCancel =
              new ActionCancel.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>>(
                  this._root, this, "actionCancel")
          : this.actionCancel);
    }

    public ActionUpdate.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>> actionUpdate() {
      return ((this.actionUpdate == null)
          ? this.actionUpdate =
              new ActionUpdate.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>>(
                  this._root, this, "actionUpdate")
          : this.actionUpdate);
    }

    public ActionRequest.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>>
        actionRequest() {
      return ((this.actionRequest == null)
          ? this.actionRequest =
              new ActionRequest.Selector<TRoot, ActionInstruction.Selector<TRoot, TParent>>(
                  this._root, this, "actionRequest")
          : this.actionRequest);
    }
  }
}