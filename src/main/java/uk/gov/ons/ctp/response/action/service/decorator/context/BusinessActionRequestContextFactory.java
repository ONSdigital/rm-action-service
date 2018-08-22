package uk.gov.ons.ctp.response.action.service.decorator.context;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.party.representation.Association;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

@Component
@Qualifier("business")
public class BusinessActionRequestContextFactory implements ActionRequestContextFactory {
  private static final Logger log =
      LoggerFactory.getLogger(BusinessActionRequestContextFactory.class);

  private final PartySvcClientService partySvcClientService;

  private final DefaultActionRequestContextFactory defaultFactory;

  public BusinessActionRequestContextFactory(
      PartySvcClientService partySvcClientService,
      DefaultActionRequestContextFactory defaultFactory) {
    this.partySvcClientService = partySvcClientService;
    this.defaultFactory = defaultFactory;
  }

  @Override
  public ActionRequestContext getActionRequestDecoratorContext(Action action) {
    ActionRequestContext context = this.defaultFactory.getActionRequestDecoratorContext(action);

    if (isRespondent(context)) {
      setPartiesForRespondent(context);
    } else {
      setPartiesForBusiness(context);
    }

    return context;
  }

  private boolean isRespondent(ActionRequestContext context) {
    return context.getAction().getActionRuleFK() == 5 || context.getAction().getActionRuleFK() == 7;
  }

  private void setPartiesForRespondent(ActionRequestContext context) {
    final PartyDTO businessParty;
    List<PartyDTO> respondentParties;

    PartyDTO childParty =
        partySvcClientService.getParty(
            context.getSampleUnitType().name(), context.getAction().getPartyId());
    log.debug("childParty retrieved is {}", childParty);
    respondentParties = Collections.singletonList(childParty);

    final UUID associatedParentPartyID = context.getCaseDetails().getCaseGroup().getPartyId();

    businessParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SampleUnitType.B.toString(), associatedParentPartyID, context.getSurvey().getId());
    log.debug("businessParty for the child retrieved is {}", businessParty);

    context.setParentParty(businessParty);
    context.setChildParties(respondentParties);
  }

  private void setPartiesForBusiness(ActionRequestContext context) {
    final PartyDTO parentParty;
    List<PartyDTO> childParties;

    parentParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            context.getSampleUnitType().name(),
            context.getCaseDetails().getPartyId(),
            context.getSurvey().getId());
    log.debug("parentParty retrieved is {}", parentParty);
    childParties = getChildParties(parentParty, context.getSampleUnitType());

    context.setParentParty(parentParty);
    context.setChildParties(childParties);
  }

  public List<PartyDTO> getChildParties(
      final PartyDTO parentParty, final SampleUnitType parentUnitType) {
    List<PartyDTO> childParties = new ArrayList<>();

    final List<String> childPartyIds =
        parentParty
            .getAssociations()
            .stream()
            .map(Association::getPartyId)
            .collect(Collectors.toList());

    // ALl child parties are parent+I, i.e B & BI.
    SampleUnitType childUnitType = parentUnitType.getChild();

    for (final String id : childPartyIds) {
      final PartyDTO childParty = partySvcClientService.getParty(childUnitType.name(), id);
      if (childParty != null) {
        childParties.add(childParty);
      } else {
        log.info("Unable to get party with id, {}", id);
      }
    }
    return childParties;
  }
}
