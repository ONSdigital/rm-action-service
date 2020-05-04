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
 * Java class for ActionUpdate complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionUpdate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ons.gov.uk/ctp/response/action/message/instruction}Action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="priority" type="{http://ons.gov.uk/ctp/response/action/message/instruction}Priority"/&gt;
 *         &lt;element name="events" type="{http://ons.gov.uk/ctp/response/action/message/instruction}ActionEvent"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionUpdate",
    propOrder = {"priority", "events"})
public class ActionUpdate extends Action implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;

  @XmlElement(required = true)
  @XmlSchemaType(name = "string")
  protected Priority priority;

  @XmlElement(required = true)
  protected ActionEvent events;

  /** Default no-arg constructor */
  public ActionUpdate() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionUpdate(
      final String actionId,
      final boolean responseRequired,
      final Priority priority,
      final ActionEvent events) {
    super(actionId, responseRequired);
    this.priority = priority;
    this.events = events;
  }

  /**
   * Gets the value of the priority property.
   *
   * @return possible object is {@link Priority }
   */
  public Priority getPriority() {
    return priority;
  }

  /**
   * Sets the value of the priority property.
   *
   * @param value allowed object is {@link Priority }
   */
  public void setPriority(Priority value) {
    this.priority = value;
  }

  /**
   * Gets the value of the events property.
   *
   * @return possible object is {@link ActionEvent }
   */
  public ActionEvent getEvents() {
    return events;
  }

  /**
   * Sets the value of the events property.
   *
   * @param value allowed object is {@link ActionEvent }
   */
  public void setEvents(ActionEvent value) {
    this.events = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionUpdate.Builder<_B> _other) {
    super.copyTo(_other);
    _other.priority = this.priority;
    _other.events = ((this.events == null) ? null : this.events.newCopyBuilder(_other));
  }

  @Override
  public <_B> ActionUpdate.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionUpdate.Builder<_B>(_parentBuilder, this, true);
  }

  @Override
  public ActionUpdate.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionUpdate.Builder<Void> builder() {
    return new ActionUpdate.Builder<Void>(null, null, false);
  }

  public static <_B> ActionUpdate.Builder<_B> copyOf(final Action _other) {
    final ActionUpdate.Builder<_B> _newBuilder = new ActionUpdate.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder);
    return _newBuilder;
  }

  public static <_B> ActionUpdate.Builder<_B> copyOf(final ActionUpdate _other) {
    final ActionUpdate.Builder<_B> _newBuilder = new ActionUpdate.Builder<_B>(null, null, false);
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
      final ActionUpdate.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    super.copyTo(_other, _propertyTree, _propertyTreeUse);
    final PropertyTree priorityPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("priority"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (priorityPropertyTree != null)
        : ((priorityPropertyTree == null) || (!priorityPropertyTree.isLeaf())))) {
      _other.priority = this.priority;
    }
    final PropertyTree eventsPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("events"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (eventsPropertyTree != null)
        : ((eventsPropertyTree == null) || (!eventsPropertyTree.isLeaf())))) {
      _other.events =
          ((this.events == null)
              ? null
              : this.events.newCopyBuilder(_other, eventsPropertyTree, _propertyTreeUse));
    }
  }

  @Override
  public <_B> ActionUpdate.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionUpdate.Builder<_B>(
        _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  @Override
  public ActionUpdate.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionUpdate.Builder<_B> copyOf(
      final Action _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionUpdate.Builder<_B> _newBuilder = new ActionUpdate.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static <_B> ActionUpdate.Builder<_B> copyOf(
      final ActionUpdate _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionUpdate.Builder<_B> _newBuilder = new ActionUpdate.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionUpdate.Builder<Void> copyExcept(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionUpdate.Builder<Void> copyExcept(
      final ActionUpdate _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionUpdate.Builder<Void> copyOnly(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public static ActionUpdate.Builder<Void> copyOnly(
      final ActionUpdate _other, final PropertyTree _propertyTree) {
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
    final ActionUpdate that = ((ActionUpdate) object);
    {
      Priority leftPriority;
      leftPriority = this.getPriority();
      Priority rightPriority;
      rightPriority = that.getPriority();
      if (this.priority != null) {
        if (that.priority != null) {
          if (!leftPriority.equals(rightPriority)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.priority != null) {
          return false;
        }
      }
    }
    {
      ActionEvent leftEvents;
      leftEvents = this.getEvents();
      ActionEvent rightEvents;
      rightEvents = that.getEvents();
      if (this.events != null) {
        if (that.events != null) {
          if (!leftEvents.equals(rightEvents)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.events != null) {
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
      Priority thePriority;
      thePriority = this.getPriority();
      if (this.priority != null) {
        currentHashCode += thePriority.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      ActionEvent theEvents;
      theEvents = this.getEvents();
      if (this.events != null) {
        currentHashCode += theEvents.hashCode();
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
      Priority thePriority;
      thePriority = this.getPriority();
      strategy.appendField(locator, this, "priority", buffer, thePriority, (this.priority != null));
    }
    {
      ActionEvent theEvents;
      theEvents = this.getEvents();
      strategy.appendField(locator, this, "events", buffer, theEvents, (this.events != null));
    }
    return buffer;
  }

  public static class Builder<_B> extends Action.Builder<_B> implements Buildable {

    private Priority priority;
    private ActionEvent.Builder<ActionUpdate.Builder<_B>> events;

    public Builder(final _B _parentBuilder, final ActionUpdate _other, final boolean _copy) {
      super(_parentBuilder, _other, _copy);
      if (_other != null) {
        this.priority = _other.priority;
        this.events = ((_other.events == null) ? null : _other.events.newCopyBuilder(this));
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionUpdate _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      super(_parentBuilder, _other, _copy, _propertyTree, _propertyTreeUse);
      if (_other != null) {
        final PropertyTree priorityPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("priority"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (priorityPropertyTree != null)
            : ((priorityPropertyTree == null) || (!priorityPropertyTree.isLeaf())))) {
          this.priority = _other.priority;
        }
        final PropertyTree eventsPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("events"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (eventsPropertyTree != null)
            : ((eventsPropertyTree == null) || (!eventsPropertyTree.isLeaf())))) {
          this.events =
              ((_other.events == null)
                  ? null
                  : _other.events.newCopyBuilder(this, eventsPropertyTree, _propertyTreeUse));
        }
      }
    }

    protected <_P extends ActionUpdate> _P init(final _P _product) {
      _product.priority = this.priority;
      _product.events = ((this.events == null) ? null : this.events.build());
      return super.init(_product);
    }

    /**
     * Sets the new value of "priority" (any previous value will be replaced)
     *
     * @param priority New value of the "priority" property.
     */
    public ActionUpdate.Builder<_B> withPriority(final Priority priority) {
      this.priority = priority;
      return this;
    }

    /**
     * Sets the new value of "events" (any previous value will be replaced)
     *
     * @param events New value of the "events" property.
     */
    public ActionUpdate.Builder<_B> withEvents(final ActionEvent events) {
      this.events =
          ((events == null)
              ? null
              : new ActionEvent.Builder<ActionUpdate.Builder<_B>>(this, events, false));
      return this;
    }

    /**
     * Returns a new builder to build the value of the "events" property (replacing previous value).
     * Use {@link uk.gov.ons.ctp.response.action.message.instruction.ActionEvent.Builder#end()} to
     * return to the current builder.
     *
     * @return A new builder to build the value of the "events" property. Use {@link
     *     uk.gov.ons.ctp.response.action.message.instruction.ActionEvent.Builder#end()} to return
     *     to the current builder.
     */
    public ActionEvent.Builder<? extends ActionUpdate.Builder<_B>> withEvents() {
      return this.events = new ActionEvent.Builder<ActionUpdate.Builder<_B>>(this, null, false);
    }

    /**
     * Sets the new value of "actionId" (any previous value will be replaced)
     *
     * @param actionId New value of the "actionId" property.
     */
    @Override
    public ActionUpdate.Builder<_B> withActionId(final String actionId) {
      super.withActionId(actionId);
      return this;
    }

    /**
     * Sets the new value of "responseRequired" (any previous value will be replaced)
     *
     * @param responseRequired New value of the "responseRequired" property.
     */
    @Override
    public ActionUpdate.Builder<_B> withResponseRequired(final boolean responseRequired) {
      super.withResponseRequired(responseRequired);
      return this;
    }

    @Override
    public ActionUpdate build() {
      if (_storedValue == null) {
        return this.init(new ActionUpdate());
      } else {
        return ((ActionUpdate) _storedValue);
      }
    }
  }

  public static class Select extends ActionUpdate.Selector<ActionUpdate.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionUpdate.Select _root() {
      return new ActionUpdate.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends Action.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, ActionUpdate.Selector<TRoot, TParent>> priority =
        null;
    private ActionEvent.Selector<TRoot, ActionUpdate.Selector<TRoot, TParent>> events = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.priority != null) {
        products.put("priority", this.priority.init());
      }
      if (this.events != null) {
        products.put("events", this.events.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionUpdate.Selector<TRoot, TParent>> priority() {
      return ((this.priority == null)
          ? this.priority =
              new com.kscs.util.jaxb.Selector<TRoot, ActionUpdate.Selector<TRoot, TParent>>(
                  this._root, this, "priority")
          : this.priority);
    }

    public ActionEvent.Selector<TRoot, ActionUpdate.Selector<TRoot, TParent>> events() {
      return ((this.events == null)
          ? this.events =
              new ActionEvent.Selector<TRoot, ActionUpdate.Selector<TRoot, TParent>>(
                  this._root, this, "events")
          : this.events);
    }
  }
}
