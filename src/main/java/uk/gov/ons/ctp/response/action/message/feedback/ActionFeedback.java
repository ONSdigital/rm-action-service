package uk.gov.ons.ctp.response.action.message.feedback;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * Java class for ActionFeedback complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionFeedback"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="actionId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="situation"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="outcome" type="{http://ons.gov.uk/ctp/response/action/message/feedback}Outcome"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionFeedback",
    propOrder = {"actionId", "situation", "outcome"})
@XmlRootElement(name = "actionFeedback")
public class ActionFeedback implements ToString2 {

  @XmlElement(required = true)
  protected String actionId;

  @XmlElement(required = true)
  protected String situation;

  @XmlElement(required = true)
  @XmlSchemaType(name = "string")
  protected Outcome outcome;

  /** Default no-arg constructor */
  public ActionFeedback() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionFeedback(final String actionId, final String situation, final Outcome outcome) {
    this.actionId = actionId;
    this.situation = situation;
    this.outcome = outcome;
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

  /**
   * Gets the value of the situation property.
   *
   * @return possible object is {@link String }
   */
  public String getSituation() {
    return situation;
  }

  /**
   * Sets the value of the situation property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSituation(String value) {
    this.situation = value;
  }

  /**
   * Gets the value of the outcome property.
   *
   * @return possible object is {@link Outcome }
   */
  public Outcome getOutcome() {
    return outcome;
  }

  /**
   * Sets the value of the outcome property.
   *
   * @param value allowed object is {@link Outcome }
   */
  public void setOutcome(Outcome value) {
    this.outcome = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionFeedback.Builder<_B> _other) {
    _other.actionId = this.actionId;
    _other.situation = this.situation;
    _other.outcome = this.outcome;
  }

  public <_B> ActionFeedback.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionFeedback.Builder<_B>(_parentBuilder, this, true);
  }

  public ActionFeedback.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionFeedback.Builder<Void> builder() {
    return new ActionFeedback.Builder<Void>(null, null, false);
  }

  public static <_B> ActionFeedback.Builder<_B> copyOf(final ActionFeedback _other) {
    final ActionFeedback.Builder<_B> _newBuilder =
        new ActionFeedback.Builder<_B>(null, null, false);
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
      final ActionFeedback.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree actionIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionIdPropertyTree != null)
        : ((actionIdPropertyTree == null) || (!actionIdPropertyTree.isLeaf())))) {
      _other.actionId = this.actionId;
    }
    final PropertyTree situationPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("situation"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (situationPropertyTree != null)
        : ((situationPropertyTree == null) || (!situationPropertyTree.isLeaf())))) {
      _other.situation = this.situation;
    }
    final PropertyTree outcomePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("outcome"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (outcomePropertyTree != null)
        : ((outcomePropertyTree == null) || (!outcomePropertyTree.isLeaf())))) {
      _other.outcome = this.outcome;
    }
  }

  public <_B> ActionFeedback.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionFeedback.Builder<_B>(
        _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public ActionFeedback.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionFeedback.Builder<_B> copyOf(
      final ActionFeedback _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionFeedback.Builder<_B> _newBuilder =
        new ActionFeedback.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionFeedback.Builder<Void> copyExcept(
      final ActionFeedback _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionFeedback.Builder<Void> copyOnly(
      final ActionFeedback _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public boolean equals(Object object) {
    if ((object == null) || (this.getClass() != object.getClass())) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final ActionFeedback that = ((ActionFeedback) object);
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
      String leftSituation;
      leftSituation = this.getSituation();
      String rightSituation;
      rightSituation = that.getSituation();
      if (this.situation != null) {
        if (that.situation != null) {
          if (!leftSituation.equals(rightSituation)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.situation != null) {
          return false;
        }
      }
    }
    {
      Outcome leftOutcome;
      leftOutcome = this.getOutcome();
      Outcome rightOutcome;
      rightOutcome = that.getOutcome();
      if (this.outcome != null) {
        if (that.outcome != null) {
          if (!leftOutcome.equals(rightOutcome)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.outcome != null) {
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
      String theActionId;
      theActionId = this.getActionId();
      if (this.actionId != null) {
        currentHashCode += theActionId.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theSituation;
      theSituation = this.getSituation();
      if (this.situation != null) {
        currentHashCode += theSituation.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      Outcome theOutcome;
      theOutcome = this.getOutcome();
      if (this.outcome != null) {
        currentHashCode += theOutcome.hashCode();
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
      String theActionId;
      theActionId = this.getActionId();
      strategy.appendField(locator, this, "actionId", buffer, theActionId, (this.actionId != null));
    }
    {
      String theSituation;
      theSituation = this.getSituation();
      strategy.appendField(
          locator, this, "situation", buffer, theSituation, (this.situation != null));
    }
    {
      Outcome theOutcome;
      theOutcome = this.getOutcome();
      strategy.appendField(locator, this, "outcome", buffer, theOutcome, (this.outcome != null));
    }
    return buffer;
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final ActionFeedback _storedValue;
    private String actionId;
    private String situation;
    private Outcome outcome;

    public Builder(final _B _parentBuilder, final ActionFeedback _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.actionId = _other.actionId;
          this.situation = _other.situation;
          this.outcome = _other.outcome;
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionFeedback _other,
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
          final PropertyTree situationPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("situation"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (situationPropertyTree != null)
              : ((situationPropertyTree == null) || (!situationPropertyTree.isLeaf())))) {
            this.situation = _other.situation;
          }
          final PropertyTree outcomePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("outcome"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (outcomePropertyTree != null)
              : ((outcomePropertyTree == null) || (!outcomePropertyTree.isLeaf())))) {
            this.outcome = _other.outcome;
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

    protected <_P extends ActionFeedback> _P init(final _P _product) {
      _product.actionId = this.actionId;
      _product.situation = this.situation;
      _product.outcome = this.outcome;
      return _product;
    }

    /**
     * Sets the new value of "actionId" (any previous value will be replaced)
     *
     * @param actionId New value of the "actionId" property.
     */
    public ActionFeedback.Builder<_B> withActionId(final String actionId) {
      this.actionId = actionId;
      return this;
    }

    /**
     * Sets the new value of "situation" (any previous value will be replaced)
     *
     * @param situation New value of the "situation" property.
     */
    public ActionFeedback.Builder<_B> withSituation(final String situation) {
      this.situation = situation;
      return this;
    }

    /**
     * Sets the new value of "outcome" (any previous value will be replaced)
     *
     * @param outcome New value of the "outcome" property.
     */
    public ActionFeedback.Builder<_B> withOutcome(final Outcome outcome) {
      this.outcome = outcome;
      return this;
    }

    @Override
    public ActionFeedback build() {
      if (_storedValue == null) {
        return this.init(new ActionFeedback());
      } else {
        return ((ActionFeedback) _storedValue);
      }
    }
  }

  public static class Select extends ActionFeedback.Selector<ActionFeedback.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionFeedback.Select _root() {
      return new ActionFeedback.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>> actionId =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>> situation =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>> outcome =
        null;

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
      if (this.situation != null) {
        products.put("situation", this.situation.init());
      }
      if (this.outcome != null) {
        products.put("outcome", this.outcome.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>> actionId() {
      return ((this.actionId == null)
          ? this.actionId =
              new com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>>(
                  this._root, this, "actionId")
          : this.actionId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>> situation() {
      return ((this.situation == null)
          ? this.situation =
              new com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>>(
                  this._root, this, "situation")
          : this.situation);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>> outcome() {
      return ((this.outcome == null)
          ? this.outcome =
              new com.kscs.util.jaxb.Selector<TRoot, ActionFeedback.Selector<TRoot, TParent>>(
                  this._root, this, "outcome")
          : this.outcome);
    }
  }
}
