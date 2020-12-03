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
 * Java class for ActionRequest complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ActionRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ons.gov.uk/ctp/response/action/message/instruction}Action"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="actionPlan" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="actionType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="questionSet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="contact" type="{http://ons.gov.uk/ctp/response/action/message/instruction}ActionContact" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{http://ons.gov.uk/ctp/response/action/message/instruction}ActionAddress" minOccurs="0"/&gt;
 *         &lt;element name="legalBasis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="respondentStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="enrolmentStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="caseGroupStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="caseId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="priority" type="{http://ons.gov.uk/ctp/response/action/message/instruction}Priority" minOccurs="0"/&gt;
 *         &lt;element name="caseRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="iac" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="events" type="{http://ons.gov.uk/ctp/response/action/message/instruction}ActionEvent"/&gt;
 *         &lt;element name="exerciseRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="userDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="surveyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="surveyRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="returnByDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sampleUnitRef" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ActionRequest",
    propOrder = {
      "actionPlan",
      "actionType",
      "questionSet",
      "contact",
      "address",
      "legalBasis",
      "region",
      "respondentStatus",
      "enrolmentStatus",
      "caseGroupStatus",
      "caseId",
      "priority",
      "caseRef",
      "iac",
      "events",
      "exerciseRef",
      "userDescription",
      "surveyName",
      "surveyRef",
      "returnByDate",
      "sampleUnitRef"
    })
public class ActionRequest extends Action implements Serializable, ToString2 {

  private static final long serialVersionUID = 8297030705722814170L;
  protected String actionPlan;

  @XmlElement(required = true)
  protected String actionType;

  protected String questionSet;
  protected ActionContact contact;
  protected ActionAddress address;
  protected String legalBasis;
  protected String region;
  protected String respondentStatus;
  protected String enrolmentStatus;
  protected String caseGroupStatus;
  protected String caseId;

  @XmlSchemaType(name = "string")
  protected Priority priority;

  protected String caseRef;
  protected String iac;

  @XmlElement(required = true)
  protected ActionEvent events;

  protected String exerciseRef;
  protected String userDescription;
  protected String surveyName;
  protected String surveyRef;
  protected String returnByDate;

  @XmlElement(required = true)
  protected String sampleUnitRef;

  /** Default no-arg constructor */
  public ActionRequest() {
    super();
  }

  /** Fully-initialising value constructor */
  public ActionRequest(
      final String actionId,
      final boolean responseRequired,
      final String actionPlan,
      final String actionType,
      final String questionSet,
      final ActionContact contact,
      final ActionAddress address,
      final String legalBasis,
      final String region,
      final String respondentStatus,
      final String enrolmentStatus,
      final String caseGroupStatus,
      final String caseId,
      final Priority priority,
      final String caseRef,
      final String iac,
      final ActionEvent events,
      final String exerciseRef,
      final String userDescription,
      final String surveyName,
      final String surveyRef,
      final String returnByDate,
      final String sampleUnitRef) {
    super(actionId, responseRequired);
    this.actionPlan = actionPlan;
    this.actionType = actionType;
    this.questionSet = questionSet;
    this.contact = contact;
    this.address = address;
    this.legalBasis = legalBasis;
    this.region = region;
    this.respondentStatus = respondentStatus;
    this.enrolmentStatus = enrolmentStatus;
    this.caseGroupStatus = caseGroupStatus;
    this.caseId = caseId;
    this.priority = priority;
    this.caseRef = caseRef;
    this.iac = iac;
    this.events = events;
    this.exerciseRef = exerciseRef;
    this.userDescription = userDescription;
    this.surveyName = surveyName;
    this.surveyRef = surveyRef;
    this.returnByDate = returnByDate;
    this.sampleUnitRef = sampleUnitRef;
  }
  /**
   * Gets the value of the actionPlan property.
   *
   * @return possible object is {@link String }
   */
  public String getActionPlan() {
    return actionPlan;
  }

  /**
   * Sets the value of the actionPlan property.
   *
   * @param value allowed object is {@link String }
   */
  public void setActionPlan(String value) {
    this.actionPlan = value;
  }

  /**
   * Gets the value of the actionType property.
   *
   * @return possible object is {@link String }
   */
  public String getActionType() {
    return actionType;
  }

  /**
   * Sets the value of the actionType property.
   *
   * @param value allowed object is {@link String }
   */
  public void setActionType(String value) {
    this.actionType = value;
  }

  /**
   * Gets the value of the questionSet property.
   *
   * @return possible object is {@link String }
   */
  public String getQuestionSet() {
    return questionSet;
  }

  /**
   * Sets the value of the questionSet property.
   *
   * @param value allowed object is {@link String }
   */
  public void setQuestionSet(String value) {
    this.questionSet = value;
  }

  /**
   * Gets the value of the contact property.
   *
   * @return possible object is {@link ActionContact }
   */
  public ActionContact getContact() {
    return contact;
  }

  /**
   * Sets the value of the contact property.
   *
   * @param value allowed object is {@link ActionContact }
   */
  public void setContact(ActionContact value) {
    this.contact = value;
  }

  /**
   * Gets the value of the address property.
   *
   * @return possible object is {@link ActionAddress }
   */
  public ActionAddress getAddress() {
    return address;
  }

  /**
   * Sets the value of the address property.
   *
   * @param value allowed object is {@link ActionAddress }
   */
  public void setAddress(ActionAddress value) {
    this.address = value;
  }

  /**
   * Gets the value of the legalBasis property.
   *
   * @return possible object is {@link String }
   */
  public String getLegalBasis() {
    return legalBasis;
  }

  /**
   * Sets the value of the legalBasis property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLegalBasis(String value) {
    this.legalBasis = value;
  }

  /**
   * Gets the value of the region property.
   *
   * @return possible object is {@link String }
   */
  public String getRegion() {
    return region;
  }

  /**
   * Sets the value of the region property.
   *
   * @param value allowed object is {@link String }
   */
  public void setRegion(String value) {
    this.region = value;
  }

  /**
   * Gets the value of the respondentStatus property.
   *
   * @return possible object is {@link String }
   */
  public String getRespondentStatus() {
    return respondentStatus;
  }

  /**
   * Sets the value of the respondentStatus property.
   *
   * @param value allowed object is {@link String }
   */
  public void setRespondentStatus(String value) {
    this.respondentStatus = value;
  }

  /**
   * Gets the value of the enrolmentStatus property.
   *
   * @return possible object is {@link String }
   */
  public String getEnrolmentStatus() {
    return enrolmentStatus;
  }

  /**
   * Sets the value of the enrolmentStatus property.
   *
   * @param value allowed object is {@link String }
   */
  public void setEnrolmentStatus(String value) {
    this.enrolmentStatus = value;
  }

  /**
   * Gets the value of the caseGroupStatus property.
   *
   * @return possible object is {@link String }
   */
  public String getCaseGroupStatus() {
    return caseGroupStatus;
  }

  /**
   * Sets the value of the caseGroupStatus property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCaseGroupStatus(String value) {
    this.caseGroupStatus = value;
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
   * Gets the value of the iac property.
   *
   * @return possible object is {@link String }
   */
  public String getIac() {
    return iac;
  }

  /**
   * Sets the value of the iac property.
   *
   * @param value allowed object is {@link String }
   */
  public void setIac(String value) {
    this.iac = value;
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
   * Gets the value of the exerciseRef property.
   *
   * @return possible object is {@link String }
   */
  public String getExerciseRef() {
    return exerciseRef;
  }

  /**
   * Sets the value of the exerciseRef property.
   *
   * @param value allowed object is {@link String }
   */
  public void setExerciseRef(String value) {
    this.exerciseRef = value;
  }

  /**
   * Gets the value of the userDescription property.
   *
   * @return possible object is {@link String }
   */
  public String getUserDescription() {
    return userDescription;
  }

  /**
   * Sets the value of the userDescription property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUserDescription(String value) {
    this.userDescription = value;
  }

  /**
   * Gets the value of the surveyName property.
   *
   * @return possible object is {@link String }
   */
  public String getSurveyName() {
    return surveyName;
  }

  /**
   * Sets the value of the surveyName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSurveyName(String value) {
    this.surveyName = value;
  }

  /**
   * Gets the value of the surveyRef property.
   *
   * @return possible object is {@link String }
   */
  public String getSurveyRef() {
    return surveyRef;
  }

  /**
   * Sets the value of the surveyRef property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSurveyRef(String value) {
    this.surveyRef = value;
  }

  /**
   * Gets the value of the returnByDate property.
   *
   * @return possible object is {@link String }
   */
  public String getReturnByDate() {
    return returnByDate;
  }

  /**
   * Sets the value of the returnByDate property.
   *
   * @param value allowed object is {@link String }
   */
  public void setReturnByDate(String value) {
    this.returnByDate = value;
  }

  /**
   * Gets the value of the sampleUnitRef property.
   *
   * @return possible object is {@link String }
   */
  public String getSampleUnitRef() {
    return sampleUnitRef;
  }

  /**
   * Sets the value of the sampleUnitRef property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSampleUnitRef(String value) {
    this.sampleUnitRef = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final ActionRequest.Builder<_B> _other) {
    super.copyTo(_other);
    _other.actionPlan = this.actionPlan;
    _other.actionType = this.actionType;
    _other.questionSet = this.questionSet;
    _other.contact = ((this.contact == null) ? null : this.contact.newCopyBuilder(_other));
    _other.address = ((this.address == null) ? null : this.address.newCopyBuilder(_other));
    _other.legalBasis = this.legalBasis;
    _other.region = this.region;
    _other.respondentStatus = this.respondentStatus;
    _other.enrolmentStatus = this.enrolmentStatus;
    _other.caseGroupStatus = this.caseGroupStatus;
    _other.caseId = this.caseId;
    _other.priority = this.priority;
    _other.caseRef = this.caseRef;
    _other.iac = this.iac;
    _other.events = ((this.events == null) ? null : this.events.newCopyBuilder(_other));
    _other.exerciseRef = this.exerciseRef;
    _other.userDescription = this.userDescription;
    _other.surveyName = this.surveyName;
    _other.surveyRef = this.surveyRef;
    _other.returnByDate = this.returnByDate;
    _other.sampleUnitRef = this.sampleUnitRef;
  }

  @Override
  public <_B> ActionRequest.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new ActionRequest.Builder<_B>(_parentBuilder, this, true);
  }

  @Override
  public ActionRequest.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static ActionRequest.Builder<Void> builder() {
    return new ActionRequest.Builder<Void>(null, null, false);
  }

  public static <_B> ActionRequest.Builder<_B> copyOf(final Action _other) {
    final ActionRequest.Builder<_B> _newBuilder = new ActionRequest.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder);
    return _newBuilder;
  }

  public static <_B> ActionRequest.Builder<_B> copyOf(final ActionRequest _other) {
    final ActionRequest.Builder<_B> _newBuilder = new ActionRequest.Builder<_B>(null, null, false);
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
      final ActionRequest.Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    super.copyTo(_other, _propertyTree, _propertyTreeUse);
    final PropertyTree actionPlanPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionPlan"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionPlanPropertyTree != null)
        : ((actionPlanPropertyTree == null) || (!actionPlanPropertyTree.isLeaf())))) {
      _other.actionPlan = this.actionPlan;
    }
    final PropertyTree actionTypePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionType"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionTypePropertyTree != null)
        : ((actionTypePropertyTree == null) || (!actionTypePropertyTree.isLeaf())))) {
      _other.actionType = this.actionType;
    }
    final PropertyTree questionSetPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("questionSet"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (questionSetPropertyTree != null)
        : ((questionSetPropertyTree == null) || (!questionSetPropertyTree.isLeaf())))) {
      _other.questionSet = this.questionSet;
    }
    final PropertyTree contactPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("contact"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (contactPropertyTree != null)
        : ((contactPropertyTree == null) || (!contactPropertyTree.isLeaf())))) {
      _other.contact =
          ((this.contact == null)
              ? null
              : this.contact.newCopyBuilder(_other, contactPropertyTree, _propertyTreeUse));
    }
    final PropertyTree addressPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("address"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (addressPropertyTree != null)
        : ((addressPropertyTree == null) || (!addressPropertyTree.isLeaf())))) {
      _other.address =
          ((this.address == null)
              ? null
              : this.address.newCopyBuilder(_other, addressPropertyTree, _propertyTreeUse));
    }
    final PropertyTree legalBasisPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("legalBasis"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (legalBasisPropertyTree != null)
        : ((legalBasisPropertyTree == null) || (!legalBasisPropertyTree.isLeaf())))) {
      _other.legalBasis = this.legalBasis;
    }
    final PropertyTree regionPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("region"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (regionPropertyTree != null)
        : ((regionPropertyTree == null) || (!regionPropertyTree.isLeaf())))) {
      _other.region = this.region;
    }
    final PropertyTree respondentStatusPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("respondentStatus"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (respondentStatusPropertyTree != null)
        : ((respondentStatusPropertyTree == null) || (!respondentStatusPropertyTree.isLeaf())))) {
      _other.respondentStatus = this.respondentStatus;
    }
    final PropertyTree enrolmentStatusPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("enrolmentStatus"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (enrolmentStatusPropertyTree != null)
        : ((enrolmentStatusPropertyTree == null) || (!enrolmentStatusPropertyTree.isLeaf())))) {
      _other.enrolmentStatus = this.enrolmentStatus;
    }
    final PropertyTree caseGroupStatusPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("caseGroupStatus"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (caseGroupStatusPropertyTree != null)
        : ((caseGroupStatusPropertyTree == null) || (!caseGroupStatusPropertyTree.isLeaf())))) {
      _other.caseGroupStatus = this.caseGroupStatus;
    }
    final PropertyTree caseIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (caseIdPropertyTree != null)
        : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
      _other.caseId = this.caseId;
    }
    final PropertyTree priorityPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("priority"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (priorityPropertyTree != null)
        : ((priorityPropertyTree == null) || (!priorityPropertyTree.isLeaf())))) {
      _other.priority = this.priority;
    }
    final PropertyTree caseRefPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("caseRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (caseRefPropertyTree != null)
        : ((caseRefPropertyTree == null) || (!caseRefPropertyTree.isLeaf())))) {
      _other.caseRef = this.caseRef;
    }
    final PropertyTree iacPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("iac"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (iacPropertyTree != null)
        : ((iacPropertyTree == null) || (!iacPropertyTree.isLeaf())))) {
      _other.iac = this.iac;
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
    final PropertyTree exerciseRefPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("exerciseRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (exerciseRefPropertyTree != null)
        : ((exerciseRefPropertyTree == null) || (!exerciseRefPropertyTree.isLeaf())))) {
      _other.exerciseRef = this.exerciseRef;
    }
    final PropertyTree userDescriptionPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("userDescription"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (userDescriptionPropertyTree != null)
        : ((userDescriptionPropertyTree == null) || (!userDescriptionPropertyTree.isLeaf())))) {
      _other.userDescription = this.userDescription;
    }
    final PropertyTree surveyNamePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("surveyName"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (surveyNamePropertyTree != null)
        : ((surveyNamePropertyTree == null) || (!surveyNamePropertyTree.isLeaf())))) {
      _other.surveyName = this.surveyName;
    }
    final PropertyTree surveyRefPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("surveyRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (surveyRefPropertyTree != null)
        : ((surveyRefPropertyTree == null) || (!surveyRefPropertyTree.isLeaf())))) {
      _other.surveyRef = this.surveyRef;
    }
    final PropertyTree returnByDatePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("returnByDate"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (returnByDatePropertyTree != null)
        : ((returnByDatePropertyTree == null) || (!returnByDatePropertyTree.isLeaf())))) {
      _other.returnByDate = this.returnByDate;
    }
    final PropertyTree sampleUnitRefPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (sampleUnitRefPropertyTree != null)
        : ((sampleUnitRefPropertyTree == null) || (!sampleUnitRefPropertyTree.isLeaf())))) {
      _other.sampleUnitRef = this.sampleUnitRef;
    }
  }

  @Override
  public <_B> ActionRequest.Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new ActionRequest.Builder<_B>(
        _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  @Override
  public ActionRequest.Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> ActionRequest.Builder<_B> copyOf(
      final Action _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionRequest.Builder<_B> _newBuilder = new ActionRequest.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static <_B> ActionRequest.Builder<_B> copyOf(
      final ActionRequest _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final ActionRequest.Builder<_B> _newBuilder = new ActionRequest.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static ActionRequest.Builder<Void> copyExcept(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionRequest.Builder<Void> copyExcept(
      final ActionRequest _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static ActionRequest.Builder<Void> copyOnly(
      final Action _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public static ActionRequest.Builder<Void> copyOnly(
      final ActionRequest _other, final PropertyTree _propertyTree) {
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
    final ActionRequest that = ((ActionRequest) object);
    {
      String leftActionPlan;
      leftActionPlan = this.getActionPlan();
      String rightActionPlan;
      rightActionPlan = that.getActionPlan();
      if (this.actionPlan != null) {
        if (that.actionPlan != null) {
          if (!leftActionPlan.equals(rightActionPlan)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.actionPlan != null) {
          return false;
        }
      }
    }
    {
      String leftActionType;
      leftActionType = this.getActionType();
      String rightActionType;
      rightActionType = that.getActionType();
      if (this.actionType != null) {
        if (that.actionType != null) {
          if (!leftActionType.equals(rightActionType)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.actionType != null) {
          return false;
        }
      }
    }
    {
      String leftQuestionSet;
      leftQuestionSet = this.getQuestionSet();
      String rightQuestionSet;
      rightQuestionSet = that.getQuestionSet();
      if (this.questionSet != null) {
        if (that.questionSet != null) {
          if (!leftQuestionSet.equals(rightQuestionSet)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.questionSet != null) {
          return false;
        }
      }
    }
    {
      ActionContact leftContact;
      leftContact = this.getContact();
      ActionContact rightContact;
      rightContact = that.getContact();
      if (this.contact != null) {
        if (that.contact != null) {
          if (!leftContact.equals(rightContact)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.contact != null) {
          return false;
        }
      }
    }
    {
      ActionAddress leftAddress;
      leftAddress = this.getAddress();
      ActionAddress rightAddress;
      rightAddress = that.getAddress();
      if (this.address != null) {
        if (that.address != null) {
          if (!leftAddress.equals(rightAddress)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.address != null) {
          return false;
        }
      }
    }
    {
      String leftLegalBasis;
      leftLegalBasis = this.getLegalBasis();
      String rightLegalBasis;
      rightLegalBasis = that.getLegalBasis();
      if (this.legalBasis != null) {
        if (that.legalBasis != null) {
          if (!leftLegalBasis.equals(rightLegalBasis)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.legalBasis != null) {
          return false;
        }
      }
    }
    {
      String leftRegion;
      leftRegion = this.getRegion();
      String rightRegion;
      rightRegion = that.getRegion();
      if (this.region != null) {
        if (that.region != null) {
          if (!leftRegion.equals(rightRegion)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.region != null) {
          return false;
        }
      }
    }
    {
      String leftRespondentStatus;
      leftRespondentStatus = this.getRespondentStatus();
      String rightRespondentStatus;
      rightRespondentStatus = that.getRespondentStatus();
      if (this.respondentStatus != null) {
        if (that.respondentStatus != null) {
          if (!leftRespondentStatus.equals(rightRespondentStatus)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.respondentStatus != null) {
          return false;
        }
      }
    }
    {
      String leftEnrolmentStatus;
      leftEnrolmentStatus = this.getEnrolmentStatus();
      String rightEnrolmentStatus;
      rightEnrolmentStatus = that.getEnrolmentStatus();
      if (this.enrolmentStatus != null) {
        if (that.enrolmentStatus != null) {
          if (!leftEnrolmentStatus.equals(rightEnrolmentStatus)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.enrolmentStatus != null) {
          return false;
        }
      }
    }
    {
      String leftCaseGroupStatus;
      leftCaseGroupStatus = this.getCaseGroupStatus();
      String rightCaseGroupStatus;
      rightCaseGroupStatus = that.getCaseGroupStatus();
      if (this.caseGroupStatus != null) {
        if (that.caseGroupStatus != null) {
          if (!leftCaseGroupStatus.equals(rightCaseGroupStatus)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.caseGroupStatus != null) {
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
    {
      String leftIac;
      leftIac = this.getIac();
      String rightIac;
      rightIac = that.getIac();
      if (this.iac != null) {
        if (that.iac != null) {
          if (!leftIac.equals(rightIac)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.iac != null) {
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
    {
      String leftExerciseRef;
      leftExerciseRef = this.getExerciseRef();
      String rightExerciseRef;
      rightExerciseRef = that.getExerciseRef();
      if (this.exerciseRef != null) {
        if (that.exerciseRef != null) {
          if (!leftExerciseRef.equals(rightExerciseRef)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.exerciseRef != null) {
          return false;
        }
      }
    }
    {
      String leftUserDescription;
      leftUserDescription = this.getUserDescription();
      String rightUserDescription;
      rightUserDescription = that.getUserDescription();
      if (this.userDescription != null) {
        if (that.userDescription != null) {
          if (!leftUserDescription.equals(rightUserDescription)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.userDescription != null) {
          return false;
        }
      }
    }
    {
      String leftSurveyName;
      leftSurveyName = this.getSurveyName();
      String rightSurveyName;
      rightSurveyName = that.getSurveyName();
      if (this.surveyName != null) {
        if (that.surveyName != null) {
          if (!leftSurveyName.equals(rightSurveyName)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.surveyName != null) {
          return false;
        }
      }
    }
    {
      String leftSurveyRef;
      leftSurveyRef = this.getSurveyRef();
      String rightSurveyRef;
      rightSurveyRef = that.getSurveyRef();
      if (this.surveyRef != null) {
        if (that.surveyRef != null) {
          if (!leftSurveyRef.equals(rightSurveyRef)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.surveyRef != null) {
          return false;
        }
      }
    }
    {
      String leftReturnByDate;
      leftReturnByDate = this.getReturnByDate();
      String rightReturnByDate;
      rightReturnByDate = that.getReturnByDate();
      if (this.returnByDate != null) {
        if (that.returnByDate != null) {
          if (!leftReturnByDate.equals(rightReturnByDate)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.returnByDate != null) {
          return false;
        }
      }
    }
    {
      String leftSampleUnitRef;
      leftSampleUnitRef = this.getSampleUnitRef();
      String rightSampleUnitRef;
      rightSampleUnitRef = that.getSampleUnitRef();
      if (this.sampleUnitRef != null) {
        if (that.sampleUnitRef != null) {
          if (!leftSampleUnitRef.equals(rightSampleUnitRef)) {
            return false;
          }
        } else {
          return false;
        }
      } else {
        if (that.sampleUnitRef != null) {
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
      String theActionPlan;
      theActionPlan = this.getActionPlan();
      if (this.actionPlan != null) {
        currentHashCode += theActionPlan.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theActionType;
      theActionType = this.getActionType();
      if (this.actionType != null) {
        currentHashCode += theActionType.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theQuestionSet;
      theQuestionSet = this.getQuestionSet();
      if (this.questionSet != null) {
        currentHashCode += theQuestionSet.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      ActionContact theContact;
      theContact = this.getContact();
      if (this.contact != null) {
        currentHashCode += theContact.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      ActionAddress theAddress;
      theAddress = this.getAddress();
      if (this.address != null) {
        currentHashCode += theAddress.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theLegalBasis;
      theLegalBasis = this.getLegalBasis();
      if (this.legalBasis != null) {
        currentHashCode += theLegalBasis.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theRegion;
      theRegion = this.getRegion();
      if (this.region != null) {
        currentHashCode += theRegion.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theRespondentStatus;
      theRespondentStatus = this.getRespondentStatus();
      if (this.respondentStatus != null) {
        currentHashCode += theRespondentStatus.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theEnrolmentStatus;
      theEnrolmentStatus = this.getEnrolmentStatus();
      if (this.enrolmentStatus != null) {
        currentHashCode += theEnrolmentStatus.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theCaseGroupStatus;
      theCaseGroupStatus = this.getCaseGroupStatus();
      if (this.caseGroupStatus != null) {
        currentHashCode += theCaseGroupStatus.hashCode();
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
      Priority thePriority;
      thePriority = this.getPriority();
      if (this.priority != null) {
        currentHashCode += thePriority.hashCode();
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
    {
      currentHashCode = (currentHashCode * 31);
      String theIac;
      theIac = this.getIac();
      if (this.iac != null) {
        currentHashCode += theIac.hashCode();
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
    {
      currentHashCode = (currentHashCode * 31);
      String theExerciseRef;
      theExerciseRef = this.getExerciseRef();
      if (this.exerciseRef != null) {
        currentHashCode += theExerciseRef.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theUserDescription;
      theUserDescription = this.getUserDescription();
      if (this.userDescription != null) {
        currentHashCode += theUserDescription.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theSurveyName;
      theSurveyName = this.getSurveyName();
      if (this.surveyName != null) {
        currentHashCode += theSurveyName.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theSurveyRef;
      theSurveyRef = this.getSurveyRef();
      if (this.surveyRef != null) {
        currentHashCode += theSurveyRef.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theReturnByDate;
      theReturnByDate = this.getReturnByDate();
      if (this.returnByDate != null) {
        currentHashCode += theReturnByDate.hashCode();
      }
    }
    {
      currentHashCode = (currentHashCode * 31);
      String theSampleUnitRef;
      theSampleUnitRef = this.getSampleUnitRef();
      if (this.sampleUnitRef != null) {
        currentHashCode += theSampleUnitRef.hashCode();
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
      String theActionPlan;
      theActionPlan = this.getActionPlan();
      strategy.appendField(
          locator, this, "actionPlan", buffer, theActionPlan, (this.actionPlan != null));
    }
    {
      String theActionType;
      theActionType = this.getActionType();
      strategy.appendField(
          locator, this, "actionType", buffer, theActionType, (this.actionType != null));
    }
    {
      String theQuestionSet;
      theQuestionSet = this.getQuestionSet();
      strategy.appendField(
          locator, this, "questionSet", buffer, theQuestionSet, (this.questionSet != null));
    }
    {
      ActionContact theContact;
      theContact = this.getContact();
      strategy.appendField(locator, this, "contact", buffer, theContact, (this.contact != null));
    }
    {
      ActionAddress theAddress;
      theAddress = this.getAddress();
      strategy.appendField(locator, this, "address", buffer, theAddress, (this.address != null));
    }
    {
      String theLegalBasis;
      theLegalBasis = this.getLegalBasis();
      strategy.appendField(
          locator, this, "legalBasis", buffer, theLegalBasis, (this.legalBasis != null));
    }
    {
      String theRegion;
      theRegion = this.getRegion();
      strategy.appendField(locator, this, "region", buffer, theRegion, (this.region != null));
    }
    {
      String theRespondentStatus;
      theRespondentStatus = this.getRespondentStatus();
      strategy.appendField(
          locator,
          this,
          "respondentStatus",
          buffer,
          theRespondentStatus,
          (this.respondentStatus != null));
    }
    {
      String theEnrolmentStatus;
      theEnrolmentStatus = this.getEnrolmentStatus();
      strategy.appendField(
          locator,
          this,
          "enrolmentStatus",
          buffer,
          theEnrolmentStatus,
          (this.enrolmentStatus != null));
    }
    {
      String theCaseGroupStatus;
      theCaseGroupStatus = this.getCaseGroupStatus();
      strategy.appendField(
          locator,
          this,
          "caseGroupStatus",
          buffer,
          theCaseGroupStatus,
          (this.caseGroupStatus != null));
    }
    {
      String theCaseId;
      theCaseId = this.getCaseId();
      strategy.appendField(locator, this, "caseId", buffer, theCaseId, (this.caseId != null));
    }
    {
      Priority thePriority;
      thePriority = this.getPriority();
      strategy.appendField(locator, this, "priority", buffer, thePriority, (this.priority != null));
    }
    {
      String theCaseRef;
      theCaseRef = this.getCaseRef();
      strategy.appendField(locator, this, "caseRef", buffer, theCaseRef, (this.caseRef != null));
    }
    {
      String theIac;
      theIac = this.getIac();
      strategy.appendField(locator, this, "iac", buffer, theIac, (this.iac != null));
    }
    {
      ActionEvent theEvents;
      theEvents = this.getEvents();
      strategy.appendField(locator, this, "events", buffer, theEvents, (this.events != null));
    }
    {
      String theExerciseRef;
      theExerciseRef = this.getExerciseRef();
      strategy.appendField(
          locator, this, "exerciseRef", buffer, theExerciseRef, (this.exerciseRef != null));
    }
    {
      String theUserDescription;
      theUserDescription = this.getUserDescription();
      strategy.appendField(
          locator,
          this,
          "userDescription",
          buffer,
          theUserDescription,
          (this.userDescription != null));
    }
    {
      String theSurveyName;
      theSurveyName = this.getSurveyName();
      strategy.appendField(
          locator, this, "surveyName", buffer, theSurveyName, (this.surveyName != null));
    }
    {
      String theSurveyRef;
      theSurveyRef = this.getSurveyRef();
      strategy.appendField(
          locator, this, "surveyRef", buffer, theSurveyRef, (this.surveyRef != null));
    }
    {
      String theReturnByDate;
      theReturnByDate = this.getReturnByDate();
      strategy.appendField(
          locator, this, "returnByDate", buffer, theReturnByDate, (this.returnByDate != null));
    }
    {
      String theSampleUnitRef;
      theSampleUnitRef = this.getSampleUnitRef();
      strategy.appendField(
          locator, this, "sampleUnitRef", buffer, theSampleUnitRef, (this.sampleUnitRef != null));
    }
    return buffer;
  }

  public static class Builder<_B> extends Action.Builder<_B> implements Buildable {

    private String actionPlan;
    private String actionType;
    private String questionSet;
    private ActionContact.Builder<ActionRequest.Builder<_B>> contact;
    private ActionAddress.Builder<ActionRequest.Builder<_B>> address;
    private String legalBasis;
    private String region;
    private String respondentStatus;
    private String enrolmentStatus;
    private String caseGroupStatus;
    private String caseId;
    private Priority priority;
    private String caseRef;
    private String iac;
    private ActionEvent.Builder<ActionRequest.Builder<_B>> events;
    private String exerciseRef;
    private String userDescription;
    private String surveyName;
    private String surveyRef;
    private String returnByDate;
    private String sampleUnitRef;

    public Builder(final _B _parentBuilder, final ActionRequest _other, final boolean _copy) {
      super(_parentBuilder, _other, _copy);
      if (_other != null) {
        this.actionPlan = _other.actionPlan;
        this.actionType = _other.actionType;
        this.questionSet = _other.questionSet;
        this.contact = ((_other.contact == null) ? null : _other.contact.newCopyBuilder(this));
        this.address = ((_other.address == null) ? null : _other.address.newCopyBuilder(this));
        this.legalBasis = _other.legalBasis;
        this.region = _other.region;
        this.respondentStatus = _other.respondentStatus;
        this.enrolmentStatus = _other.enrolmentStatus;
        this.caseGroupStatus = _other.caseGroupStatus;
        this.caseId = _other.caseId;
        this.priority = _other.priority;
        this.caseRef = _other.caseRef;
        this.iac = _other.iac;
        this.events = ((_other.events == null) ? null : _other.events.newCopyBuilder(this));
        this.exerciseRef = _other.exerciseRef;
        this.userDescription = _other.userDescription;
        this.surveyName = _other.surveyName;
        this.surveyRef = _other.surveyRef;
        this.returnByDate = _other.returnByDate;
        this.sampleUnitRef = _other.sampleUnitRef;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final ActionRequest _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      super(_parentBuilder, _other, _copy, _propertyTree, _propertyTreeUse);
      if (_other != null) {
        final PropertyTree actionPlanPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("actionPlan"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (actionPlanPropertyTree != null)
            : ((actionPlanPropertyTree == null) || (!actionPlanPropertyTree.isLeaf())))) {
          this.actionPlan = _other.actionPlan;
        }
        final PropertyTree actionTypePropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("actionType"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (actionTypePropertyTree != null)
            : ((actionTypePropertyTree == null) || (!actionTypePropertyTree.isLeaf())))) {
          this.actionType = _other.actionType;
        }
        final PropertyTree questionSetPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("questionSet"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (questionSetPropertyTree != null)
            : ((questionSetPropertyTree == null) || (!questionSetPropertyTree.isLeaf())))) {
          this.questionSet = _other.questionSet;
        }
        final PropertyTree contactPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("contact"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (contactPropertyTree != null)
            : ((contactPropertyTree == null) || (!contactPropertyTree.isLeaf())))) {
          this.contact =
              ((_other.contact == null)
                  ? null
                  : _other.contact.newCopyBuilder(this, contactPropertyTree, _propertyTreeUse));
        }
        final PropertyTree addressPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("address"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (addressPropertyTree != null)
            : ((addressPropertyTree == null) || (!addressPropertyTree.isLeaf())))) {
          this.address =
              ((_other.address == null)
                  ? null
                  : _other.address.newCopyBuilder(this, addressPropertyTree, _propertyTreeUse));
        }
        final PropertyTree legalBasisPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("legalBasis"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (legalBasisPropertyTree != null)
            : ((legalBasisPropertyTree == null) || (!legalBasisPropertyTree.isLeaf())))) {
          this.legalBasis = _other.legalBasis;
        }
        final PropertyTree regionPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("region"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (regionPropertyTree != null)
            : ((regionPropertyTree == null) || (!regionPropertyTree.isLeaf())))) {
          this.region = _other.region;
        }
        final PropertyTree respondentStatusPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("respondentStatus"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (respondentStatusPropertyTree != null)
            : ((respondentStatusPropertyTree == null)
                || (!respondentStatusPropertyTree.isLeaf())))) {
          this.respondentStatus = _other.respondentStatus;
        }
        final PropertyTree enrolmentStatusPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("enrolmentStatus"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (enrolmentStatusPropertyTree != null)
            : ((enrolmentStatusPropertyTree == null) || (!enrolmentStatusPropertyTree.isLeaf())))) {
          this.enrolmentStatus = _other.enrolmentStatus;
        }
        final PropertyTree caseGroupStatusPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("caseGroupStatus"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (caseGroupStatusPropertyTree != null)
            : ((caseGroupStatusPropertyTree == null) || (!caseGroupStatusPropertyTree.isLeaf())))) {
          this.caseGroupStatus = _other.caseGroupStatus;
        }
        final PropertyTree caseIdPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (caseIdPropertyTree != null)
            : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
          this.caseId = _other.caseId;
        }
        final PropertyTree priorityPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("priority"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (priorityPropertyTree != null)
            : ((priorityPropertyTree == null) || (!priorityPropertyTree.isLeaf())))) {
          this.priority = _other.priority;
        }
        final PropertyTree caseRefPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("caseRef"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (caseRefPropertyTree != null)
            : ((caseRefPropertyTree == null) || (!caseRefPropertyTree.isLeaf())))) {
          this.caseRef = _other.caseRef;
        }
        final PropertyTree iacPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("iac"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (iacPropertyTree != null)
            : ((iacPropertyTree == null) || (!iacPropertyTree.isLeaf())))) {
          this.iac = _other.iac;
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
        final PropertyTree exerciseRefPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("exerciseRef"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (exerciseRefPropertyTree != null)
            : ((exerciseRefPropertyTree == null) || (!exerciseRefPropertyTree.isLeaf())))) {
          this.exerciseRef = _other.exerciseRef;
        }
        final PropertyTree userDescriptionPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("userDescription"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (userDescriptionPropertyTree != null)
            : ((userDescriptionPropertyTree == null) || (!userDescriptionPropertyTree.isLeaf())))) {
          this.userDescription = _other.userDescription;
        }
        final PropertyTree surveyNamePropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("surveyName"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (surveyNamePropertyTree != null)
            : ((surveyNamePropertyTree == null) || (!surveyNamePropertyTree.isLeaf())))) {
          this.surveyName = _other.surveyName;
        }
        final PropertyTree surveyRefPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("surveyRef"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (surveyRefPropertyTree != null)
            : ((surveyRefPropertyTree == null) || (!surveyRefPropertyTree.isLeaf())))) {
          this.surveyRef = _other.surveyRef;
        }
        final PropertyTree returnByDatePropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("returnByDate"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (returnByDatePropertyTree != null)
            : ((returnByDatePropertyTree == null) || (!returnByDatePropertyTree.isLeaf())))) {
          this.returnByDate = _other.returnByDate;
        }
        final PropertyTree sampleUnitRefPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitRef"));
        if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (sampleUnitRefPropertyTree != null)
            : ((sampleUnitRefPropertyTree == null) || (!sampleUnitRefPropertyTree.isLeaf())))) {
          this.sampleUnitRef = _other.sampleUnitRef;
        }
      }
    }

    protected <_P extends ActionRequest> _P init(final _P _product) {
      _product.actionPlan = this.actionPlan;
      _product.actionType = this.actionType;
      _product.questionSet = this.questionSet;
      _product.contact = ((this.contact == null) ? null : this.contact.build());
      _product.address = ((this.address == null) ? null : this.address.build());
      _product.legalBasis = this.legalBasis;
      _product.region = this.region;
      _product.respondentStatus = this.respondentStatus;
      _product.enrolmentStatus = this.enrolmentStatus;
      _product.caseGroupStatus = this.caseGroupStatus;
      _product.caseId = this.caseId;
      _product.priority = this.priority;
      _product.caseRef = this.caseRef;
      _product.iac = this.iac;
      _product.events = ((this.events == null) ? null : this.events.build());
      _product.exerciseRef = this.exerciseRef;
      _product.userDescription = this.userDescription;
      _product.surveyName = this.surveyName;
      _product.surveyRef = this.surveyRef;
      _product.returnByDate = this.returnByDate;
      _product.sampleUnitRef = this.sampleUnitRef;
      return super.init(_product);
    }

    /**
     * Sets the new value of "actionPlan" (any previous value will be replaced)
     *
     * @param actionPlan New value of the "actionPlan" property.
     */
    public ActionRequest.Builder<_B> withActionPlan(final String actionPlan) {
      this.actionPlan = actionPlan;
      return this;
    }

    /**
     * Sets the new value of "actionType" (any previous value will be replaced)
     *
     * @param actionType New value of the "actionType" property.
     */
    public ActionRequest.Builder<_B> withActionType(final String actionType) {
      this.actionType = actionType;
      return this;
    }

    /**
     * Sets the new value of "questionSet" (any previous value will be replaced)
     *
     * @param questionSet New value of the "questionSet" property.
     */
    public ActionRequest.Builder<_B> withQuestionSet(final String questionSet) {
      this.questionSet = questionSet;
      return this;
    }

    /**
     * Sets the new value of "contact" (any previous value will be replaced)
     *
     * @param contact New value of the "contact" property.
     */
    public ActionRequest.Builder<_B> withContact(final ActionContact contact) {
      this.contact =
          ((contact == null)
              ? null
              : new ActionContact.Builder<ActionRequest.Builder<_B>>(this, contact, false));
      return this;
    }

    /**
     * Returns a new builder to build the value of the "contact" property (replacing previous
     * value). Use {@link
     * uk.gov.ons.ctp.response.action.message.instruction.ActionContact.Builder#end()} to return to
     * the current builder.
     *
     * @return A new builder to build the value of the "contact" property. Use {@link
     *     uk.gov.ons.ctp.response.action.message.instruction.ActionContact.Builder#end()} to return
     *     to the current builder.
     */
    public ActionContact.Builder<? extends ActionRequest.Builder<_B>> withContact() {
      return this.contact = new ActionContact.Builder<ActionRequest.Builder<_B>>(this, null, false);
    }

    /**
     * Sets the new value of "address" (any previous value will be replaced)
     *
     * @param address New value of the "address" property.
     */
    public ActionRequest.Builder<_B> withAddress(final ActionAddress address) {
      this.address =
          ((address == null)
              ? null
              : new ActionAddress.Builder<ActionRequest.Builder<_B>>(this, address, false));
      return this;
    }

    /**
     * Returns a new builder to build the value of the "address" property (replacing previous
     * value). Use {@link
     * uk.gov.ons.ctp.response.action.message.instruction.ActionAddress.Builder#end()} to return to
     * the current builder.
     *
     * @return A new builder to build the value of the "address" property. Use {@link
     *     uk.gov.ons.ctp.response.action.message.instruction.ActionAddress.Builder#end()} to return
     *     to the current builder.
     */
    public ActionAddress.Builder<? extends ActionRequest.Builder<_B>> withAddress() {
      return this.address = new ActionAddress.Builder<ActionRequest.Builder<_B>>(this, null, false);
    }

    /**
     * Sets the new value of "legalBasis" (any previous value will be replaced)
     *
     * @param legalBasis New value of the "legalBasis" property.
     */
    public ActionRequest.Builder<_B> withLegalBasis(final String legalBasis) {
      this.legalBasis = legalBasis;
      return this;
    }

    /**
     * Sets the new value of "region" (any previous value will be replaced)
     *
     * @param region New value of the "region" property.
     */
    public ActionRequest.Builder<_B> withRegion(final String region) {
      this.region = region;
      return this;
    }

    /**
     * Sets the new value of "respondentStatus" (any previous value will be replaced)
     *
     * @param respondentStatus New value of the "respondentStatus" property.
     */
    public ActionRequest.Builder<_B> withRespondentStatus(final String respondentStatus) {
      this.respondentStatus = respondentStatus;
      return this;
    }

    /**
     * Sets the new value of "enrolmentStatus" (any previous value will be replaced)
     *
     * @param enrolmentStatus New value of the "enrolmentStatus" property.
     */
    public ActionRequest.Builder<_B> withEnrolmentStatus(final String enrolmentStatus) {
      this.enrolmentStatus = enrolmentStatus;
      return this;
    }

    /**
     * Sets the new value of "caseGroupStatus" (any previous value will be replaced)
     *
     * @param caseGroupStatus New value of the "caseGroupStatus" property.
     */
    public ActionRequest.Builder<_B> withCaseGroupStatus(final String caseGroupStatus) {
      this.caseGroupStatus = caseGroupStatus;
      return this;
    }

    /**
     * Sets the new value of "caseId" (any previous value will be replaced)
     *
     * @param caseId New value of the "caseId" property.
     */
    public ActionRequest.Builder<_B> withCaseId(final String caseId) {
      this.caseId = caseId;
      return this;
    }

    /**
     * Sets the new value of "priority" (any previous value will be replaced)
     *
     * @param priority New value of the "priority" property.
     */
    public ActionRequest.Builder<_B> withPriority(final Priority priority) {
      this.priority = priority;
      return this;
    }

    /**
     * Sets the new value of "caseRef" (any previous value will be replaced)
     *
     * @param caseRef New value of the "caseRef" property.
     */
    public ActionRequest.Builder<_B> withCaseRef(final String caseRef) {
      this.caseRef = caseRef;
      return this;
    }

    /**
     * Sets the new value of "iac" (any previous value will be replaced)
     *
     * @param iac New value of the "iac" property.
     */
    public ActionRequest.Builder<_B> withIac(final String iac) {
      this.iac = iac;
      return this;
    }

    /**
     * Sets the new value of "events" (any previous value will be replaced)
     *
     * @param events New value of the "events" property.
     */
    public ActionRequest.Builder<_B> withEvents(final ActionEvent events) {
      this.events =
          ((events == null)
              ? null
              : new ActionEvent.Builder<ActionRequest.Builder<_B>>(this, events, false));
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
    public ActionEvent.Builder<? extends ActionRequest.Builder<_B>> withEvents() {
      return this.events = new ActionEvent.Builder<ActionRequest.Builder<_B>>(this, null, false);
    }

    /**
     * Sets the new value of "exerciseRef" (any previous value will be replaced)
     *
     * @param exerciseRef New value of the "exerciseRef" property.
     */
    public ActionRequest.Builder<_B> withExerciseRef(final String exerciseRef) {
      this.exerciseRef = exerciseRef;
      return this;
    }

    /**
     * Sets the new value of "userDescription" (any previous value will be replaced)
     *
     * @param userDescription New value of the "userDescription" property.
     */
    public ActionRequest.Builder<_B> withUserDescription(final String userDescription) {
      this.userDescription = userDescription;
      return this;
    }

    /**
     * Sets the new value of "surveyName" (any previous value will be replaced)
     *
     * @param surveyName New value of the "surveyName" property.
     */
    public ActionRequest.Builder<_B> withSurveyName(final String surveyName) {
      this.surveyName = surveyName;
      return this;
    }

    /**
     * Sets the new value of "surveyRef" (any previous value will be replaced)
     *
     * @param surveyRef New value of the "surveyRef" property.
     */
    public ActionRequest.Builder<_B> withSurveyRef(final String surveyRef) {
      this.surveyRef = surveyRef;
      return this;
    }

    /**
     * Sets the new value of "returnByDate" (any previous value will be replaced)
     *
     * @param returnByDate New value of the "returnByDate" property.
     */
    public ActionRequest.Builder<_B> withReturnByDate(final String returnByDate) {
      this.returnByDate = returnByDate;
      return this;
    }

    /**
     * Sets the new value of "sampleUnitRef" (any previous value will be replaced)
     *
     * @param sampleUnitRef New value of the "sampleUnitRef" property.
     */
    public ActionRequest.Builder<_B> withSampleUnitRef(final String sampleUnitRef) {
      this.sampleUnitRef = sampleUnitRef;
      return this;
    }

    /**
     * Sets the new value of "actionId" (any previous value will be replaced)
     *
     * @param actionId New value of the "actionId" property.
     */
    @Override
    public ActionRequest.Builder<_B> withActionId(final String actionId) {
      super.withActionId(actionId);
      return this;
    }

    /**
     * Sets the new value of "responseRequired" (any previous value will be replaced)
     *
     * @param responseRequired New value of the "responseRequired" property.
     */
    @Override
    public ActionRequest.Builder<_B> withResponseRequired(final boolean responseRequired) {
      super.withResponseRequired(responseRequired);
      return this;
    }

    @Override
    public ActionRequest build() {
      if (_storedValue == null) {
        return this.init(new ActionRequest());
      } else {
        return ((ActionRequest) _storedValue);
      }
    }
  }

  public static class Select extends ActionRequest.Selector<ActionRequest.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static ActionRequest.Select _root() {
      return new ActionRequest.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends Action.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> actionPlan =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> actionType =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> questionSet =
        null;
    private ActionContact.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> contact = null;
    private ActionAddress.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> address = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> legalBasis =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> region =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        respondentStatus = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        enrolmentStatus = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        caseGroupStatus = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> caseId =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> priority =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> caseRef =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> iac = null;
    private ActionEvent.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> events = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> exerciseRef =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        userDescription = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> surveyName =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> surveyRef =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        returnByDate = null;
    private com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        sampleUnitRef = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.actionPlan != null) {
        products.put("actionPlan", this.actionPlan.init());
      }
      if (this.actionType != null) {
        products.put("actionType", this.actionType.init());
      }
      if (this.questionSet != null) {
        products.put("questionSet", this.questionSet.init());
      }
      if (this.contact != null) {
        products.put("contact", this.contact.init());
      }
      if (this.address != null) {
        products.put("address", this.address.init());
      }
      if (this.legalBasis != null) {
        products.put("legalBasis", this.legalBasis.init());
      }
      if (this.region != null) {
        products.put("region", this.region.init());
      }
      if (this.respondentStatus != null) {
        products.put("respondentStatus", this.respondentStatus.init());
      }
      if (this.enrolmentStatus != null) {
        products.put("enrolmentStatus", this.enrolmentStatus.init());
      }
      if (this.caseGroupStatus != null) {
        products.put("caseGroupStatus", this.caseGroupStatus.init());
      }
      if (this.caseId != null) {
        products.put("caseId", this.caseId.init());
      }
      if (this.priority != null) {
        products.put("priority", this.priority.init());
      }
      if (this.caseRef != null) {
        products.put("caseRef", this.caseRef.init());
      }
      if (this.iac != null) {
        products.put("iac", this.iac.init());
      }
      if (this.events != null) {
        products.put("events", this.events.init());
      }
      if (this.exerciseRef != null) {
        products.put("exerciseRef", this.exerciseRef.init());
      }
      if (this.userDescription != null) {
        products.put("userDescription", this.userDescription.init());
      }
      if (this.surveyName != null) {
        products.put("surveyName", this.surveyName.init());
      }
      if (this.surveyRef != null) {
        products.put("surveyRef", this.surveyRef.init());
      }
      if (this.returnByDate != null) {
        products.put("returnByDate", this.returnByDate.init());
      }
      if (this.sampleUnitRef != null) {
        products.put("sampleUnitRef", this.sampleUnitRef.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> actionPlan() {
      return ((this.actionPlan == null)
          ? this.actionPlan =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "actionPlan")
          : this.actionPlan);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> actionType() {
      return ((this.actionType == null)
          ? this.actionType =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "actionType")
          : this.actionType);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        questionSet() {
      return ((this.questionSet == null)
          ? this.questionSet =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "questionSet")
          : this.questionSet);
    }

    public ActionContact.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> contact() {
      return ((this.contact == null)
          ? this.contact =
              new ActionContact.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "contact")
          : this.contact);
    }

    public ActionAddress.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> address() {
      return ((this.address == null)
          ? this.address =
              new ActionAddress.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "address")
          : this.address);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> legalBasis() {
      return ((this.legalBasis == null)
          ? this.legalBasis =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "legalBasis")
          : this.legalBasis);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> region() {
      return ((this.region == null)
          ? this.region =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "region")
          : this.region);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        respondentStatus() {
      return ((this.respondentStatus == null)
          ? this.respondentStatus =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "respondentStatus")
          : this.respondentStatus);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        enrolmentStatus() {
      return ((this.enrolmentStatus == null)
          ? this.enrolmentStatus =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "enrolmentStatus")
          : this.enrolmentStatus);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        caseGroupStatus() {
      return ((this.caseGroupStatus == null)
          ? this.caseGroupStatus =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "caseGroupStatus")
          : this.caseGroupStatus);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> caseId() {
      return ((this.caseId == null)
          ? this.caseId =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "caseId")
          : this.caseId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> priority() {
      return ((this.priority == null)
          ? this.priority =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "priority")
          : this.priority);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> caseRef() {
      return ((this.caseRef == null)
          ? this.caseRef =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "caseRef")
          : this.caseRef);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> iac() {
      return ((this.iac == null)
          ? this.iac =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "iac")
          : this.iac);
    }

    public ActionEvent.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> events() {
      return ((this.events == null)
          ? this.events =
              new ActionEvent.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "events")
          : this.events);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        exerciseRef() {
      return ((this.exerciseRef == null)
          ? this.exerciseRef =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "exerciseRef")
          : this.exerciseRef);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        userDescription() {
      return ((this.userDescription == null)
          ? this.userDescription =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "userDescription")
          : this.userDescription);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> surveyName() {
      return ((this.surveyName == null)
          ? this.surveyName =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "surveyName")
          : this.surveyName);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>> surveyRef() {
      return ((this.surveyRef == null)
          ? this.surveyRef =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "surveyRef")
          : this.surveyRef);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        returnByDate() {
      return ((this.returnByDate == null)
          ? this.returnByDate =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "returnByDate")
          : this.returnByDate);
    }

    public com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>
        sampleUnitRef() {
      return ((this.sampleUnitRef == null)
          ? this.sampleUnitRef =
              new com.kscs.util.jaxb.Selector<TRoot, ActionRequest.Selector<TRoot, TParent>>(
                  this._root, this, "sampleUnitRef")
          : this.sampleUnitRef);
    }
  }
}
