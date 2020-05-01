package uk.gov.ons.ctp.response.action.message.instruction;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.io.Serializable;
import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

/**
 * Java class for ActionEvent complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="event" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionEvent",
    propOrder = {"events"})
public class ActionEvent implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;

  @XmlElement(name = "event")
  protected List<String> events;

  /** Default no-arg constructor */
  public ActionEvent() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionEvent(final List<String> events) {
    this.events = events;
  }

  /**
   * Gets the value of the events property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the events property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getEvents().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link String }
   */
  public List<String> getEvents() {
    if (events == null) {
      events = new ArrayList<String>();
    }
    return this.events;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionEvent.Builder<_B> _other) {
    if (this.events == null) {
      _other.events = null;
    } else {
      _other.events = new ArrayList<Buildable>();
      for (String _item : this.events) {
        _other.events.add(((_item == null) ? null : new Buildable.PrimitiveBuildable(_item)));
      }
    }
  }

  public <_B> ActionEvent.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionEvent.Builder<_B>(_parentBuilder, this, true);
  }

  public ActionEvent.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionEvent.Builder<Void> builder() {
    return new ActionEvent.Builder<Void>(null, null, false);
  }

  public static <_B> ActionEvent.Builder<_B> copyOf(final ActionEvent _other) {
    final ActionEvent.Builder<_B> _newBuilder = new ActionEvent.Builder<_B>(null, null, false);
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
      final ActionEvent.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree eventsPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("events"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (eventsPropertyTree != null)
        : ((eventsPropertyTree == null) || (!eventsPropertyTree.isLeaf())))) {
      if (this.events == null) {
        _other.events = null;
      } else {
        _other.events = new ArrayList<Buildable>();
        for (String _item : this.events) {
          _other.events.add(((_item == null) ? null : new Buildable.PrimitiveBuildable(_item)));
        }
      }
    }
  }

  public <_B> ActionEvent.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionEvent.Builder<_B>(_parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public ActionEvent.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionEvent.Builder<_B> copyOf(
      final ActionEvent _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionEvent.Builder<_B> _newBuilder = new ActionEvent.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionEvent.Builder<Void> copyExcept(
      final ActionEvent _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionEvent.Builder<Void> copyOnly(
      final ActionEvent _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public boolean equals(Object object) {
    if ((object == null) || (this.getClass() != object.getClass())) {
      return false;
    }
    if (this == object) {
      return true;
    }
    final ActionEvent that = ((ActionEvent) object);
    {
      List<String> leftEvents;
      leftEvents = (((this.events != null) && (!this.events.isEmpty())) ? this.getEvents() : null);
      List<String> rightEvents;
      rightEvents = (((that.events != null) && (!that.events.isEmpty())) ? that.getEvents() : null);
      if ((this.events != null) && (!this.events.isEmpty())) {
        if ((that.events != null) && (!that.events.isEmpty())) {
          if (!leftEvents.equals(rightEvents)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if ((that.events != null) && (!that.events.isEmpty())) {
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
      List<String> theEvents;
      theEvents = (((this.events != null) && (!this.events.isEmpty())) ? this.getEvents() : null);
      if ((this.events != null) && (!this.events.isEmpty())) {
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
    {
      List<String> theEvents;
      theEvents = (((this.events != null) && (!this.events.isEmpty())) ? this.getEvents() : null);
      strategy.appendField(
          locator,
          this,
          "events",
          buffer,
          theEvents,
          ((this.events != null) && (!this.events.isEmpty())));
    }
    return buffer;
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final ActionEvent _storedValue;
    private List<Buildable> events;

    public Builder(final _B _parentBuilder, final ActionEvent _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          if (_other.events == null) {
            this.events = null;
          } else {
            this.events = new ArrayList<Buildable>();
            for (String _item : _other.events) {
              this.events.add(((_item == null) ? null : new Buildable.PrimitiveBuildable(_item)));
            }
          }
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionEvent _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree eventsPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("events"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (eventsPropertyTree != null)
              : ((eventsPropertyTree == null) || (!eventsPropertyTree.isLeaf())))) {
            if (_other.events == null) {
              this.events = null;
            } else {
              this.events = new ArrayList<Buildable>();
              for (String _item : _other.events) {
                this.events.add(((_item == null) ? null : new Buildable.PrimitiveBuildable(_item)));
              }
            }
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

    protected <_P extends ActionEvent> _P init(final _P _product) {
      if (this.events != null) {
        final List<String> events = new ArrayList<String>(this.events.size());
        for (Buildable _item : this.events) {
          events.add(((String) _item.build()));
        }
        _product.events = events;
      }
      return _product;
    }

    /**
     * Adds the given items to the value of "events"
     *
     * @param events Items to add to the value of the "events" property
     */
    public ActionEvent.Builder<_B> addEvents(final Iterable<? extends String> events) {
      if (events != null) {
        if (this.events == null) {
          this.events = new ArrayList<Buildable>();
        }
        for (String _item : events) {
          this.events.add(new Buildable.PrimitiveBuildable(_item));
        }
      }
      return this;
    }

    /**
     * Sets the new value of "events" (any previous value will be replaced)
     *
     * @param events New value of the "events" property.
     */
    public ActionEvent.Builder<_B> withEvents(final Iterable<? extends String> events) {
      if (this.events != null) {
        this.events.clear();
      }
      return addEvents(events);
    }

    /**
     * Adds the given items to the value of "events"
     *
     * @param events Items to add to the value of the "events" property
     */
    public ActionEvent.Builder<_B> addEvents(String... events) {
      addEvents(Arrays.asList(events));
      return this;
    }

    /**
     * Sets the new value of "events" (any previous value will be replaced)
     *
     * @param events New value of the "events" property.
     */
    public ActionEvent.Builder<_B> withEvents(String... events) {
      withEvents(Arrays.asList(events));
      return this;
    }

    @Override
    public ActionEvent build() {
      if (_storedValue == null) {
        return this.init(new ActionEvent());
      } else {
        return ((ActionEvent) _storedValue);
      }
    }
  }

  public static class Select extends ActionEvent.Selector<ActionEvent.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionEvent.Select _root() {
      return new ActionEvent.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, ActionEvent.Selector<TRoot, TParent>> events = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.events != null) {
        products.put("events", this.events.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionEvent.Selector<TRoot, TParent>> events() {
      return ((this.events == null)
          ? this.events =
              new com.kscs.util.jaxb.Selector<TRoot, ActionEvent.Selector<TRoot, TParent>>(
                  this._root, this, "events")
          : this.events);
    }
  }
}
