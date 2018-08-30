package uk.gov.ons.ctp.response.action.service.decorator.context;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private PartySvcClientService partySvcClientService;

  @Autowired private DefaultActionRequestContextFactory defaultFactory;

  @Override
  public ActionRequestContext getActionRequestDecoratorContext(Action action) {
    ActionRequestContext context = this.defaultFactory.getActionRequestDecoratorContext(action);

    if (isIndividual(context)) {
      setPartiesForIndividual(context);
    } else {
      setPartiesForBusiness(context);
    }

    return context;
  }

  private boolean isIndividual(ActionRequestContext context) {
    return context.getSampleUnitType().isParent() == false;
  }

  private void setPartiesForIndividual(ActionRequestContext context) {
    final PartyDTO parentParty;
    List<PartyDTO> childParties;

    PartyDTO childParty =
        partySvcClientService.getParty(
            context.getSampleUnitType().name(), context.getCaseDetails().getPartyId());
    log.with("child_party", childParty).debug("childParty retrieved ");
    childParties = Collections.singletonList(childParty);

    final UUID associatedParentPartyID = context.getCaseDetails().getCaseGroup().getPartyId();
    // For BRES, child sampleUnitTypeStr is BI. parent will thus be B.
    parentParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            context.getSampleUnitType().name().substring(0, 1),
            associatedParentPartyID,
            context.getSurvey().getId());
    log.with("parent_party", parentParty).debug("parentParty for the child retrieved");

    context.setParentParty(parentParty);
    context.setChildParties(childParties);
  }

  private void setPartiesForBusiness(ActionRequestContext context) {
    final PartyDTO parentParty;
    List<PartyDTO> childParties;

    parentParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            context.getSampleUnitType().name(),
            context.getCaseDetails().getPartyId(),
            context.getSurvey().getId());
    log.with("parent_party", parentParty).debug("parentParty retrieved");
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
        log.with("party_id", id).info("Unable to get party with id");
      }
    }
    return childParties;
  }
}
